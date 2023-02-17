package ru.yandex.statsservice.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.statsservice.dto.Stat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Setter
@Getter
@NamedNativeQueries({
        @NamedNativeQuery(name = "GetNotUniqueIpStat", resultSetMapping = "HitToDtoMapping",
                query = "select h.app, h.uri, count(h.ip) as hits " +
                        "from hits as h " +
                        "where (h.timeStamp between :start and :end) " +
                        "and (h.uri in :uris) " +
                        "group by h.app, h.uri order by hits desc "
                ),
        @NamedNativeQuery(name = "GetUniqueIpStat", resultSetMapping = "HitToDtoMapping",
                query = "select h.app, h.uri, count(distinct h.ip) as hits " +
                        "from hits as h " +
                        "where (h.timeStamp between :start and :end) " +
                        "and (h.uri in :uris) " +
                        "group by h.app, h.uri order by hits desc "
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
    @Column(name = "app")
    private String app;
    @Column(name = "uri")
    private String uri;
    @Column(name = "ip")
    private String ip;
    @Column(name = "timestamp")
    private LocalDateTime timeStamp;
}
