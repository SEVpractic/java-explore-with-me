package ru.practicum.statsservice.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.statsdto.dto.Stat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Setter
@Getter
@NamedNativeQueries({
        @NamedNativeQuery(name = "GetNotUniqueIpStat", resultSetMapping = "HitToDtoMapping",
                query = "select a.app_name as app, h.uri, count(h.ip) as hits " +
                        "from hits as h join apps a on h.app_id = a.app_id " +
                        "where (h.timeStamp between :start and :end) " +
                        "and (h.uri in :uris) " +
                        "group by h.uri, a.app_name order by hits desc "
                ),
        @NamedNativeQuery(name = "GetUniqueIpStat", resultSetMapping = "HitToDtoMapping",
                query = "select a.app_name as app, h.uri, count(distinct h.ip) as hits " +
                        "from hits as h join apps a on h.app_id = a.app_id " +
                        "where (h.timeStamp between :start and :end) " +
                        "and (h.uri in :uris) " +
                        "group by h.uri, a.app_name order by hits desc "
                ),
        @NamedNativeQuery(name = "GetNotUniqueIpStatNoUri", resultSetMapping = "HitToDtoMapping",
                query = "select a.app_name as app, h.uri, count(h.ip) as hits " +
                        "from hits as h join apps a on h.app_id = a.app_id " +
                        "where (h.timeStamp between :start and :end) " +
                        "group by h.uri, a.app_name order by hits desc "
                ),
        @NamedNativeQuery(name = "GetUniqueIpStatNoUri", resultSetMapping = "HitToDtoMapping",
                query = "select a.app_name as app, h.uri, count(distinct h.ip) as hits " +
                        "from hits as h join apps a on h.app_id = a.app_id " +
                        "where (h.timeStamp between :start and :end) " +
                        "group by h.uri, a.app_name order by hits desc "
                )
})
@SqlResultSetMapping(name = "HitToDtoMapping",
        classes = {
                @ConstructorResult(
                        targetClass = Stat.class,
                        columns = {
                                @ColumnResult(name = "app", type = String.class),
                                @ColumnResult(name = "uri", type = String.class),
                                @ColumnResult(name = "hits", type = Integer.class)
                        }
                )}
)
public class Hit {
    @Id
    @Column(name = "hit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "app_id")
    private App app;
    @Column(name = "uri")
    private String uri;
    @Column(name = "ip")
    private String ip;
    @Column(name = "timestamp")
    private LocalDateTime timeStamp;
}
