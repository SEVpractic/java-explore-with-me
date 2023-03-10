package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.event.dto.AdminCommentDto;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.model.AdminComment;
import ru.practicum.ewmservice.event.model.Event;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class AdminCommentMapper {

    public static AdminComment toAdminComment(EventIncomeDto dto, Event event) {
        AdminComment adminComment = new AdminComment();

        adminComment.setText(dto.getComment());
        adminComment.setEvent(event);

        return adminComment;
    }

    public static List<AdminComment> toAdminComment(List<EventIncomeDto> dto, Map<Long, Event> events) {
        return dto.stream()
                .map(d -> toAdminComment(d, events.get(d.getEventId())))
                .collect(Collectors.toList());
    }

    public static AdminCommentDto toAdminCommentDto(AdminComment comment) {
        return AdminCommentDto.builder()
                .commentId(comment.getCommentId())
                .createdOn(comment.getCreatedOn())
                .text(comment.getText())
                .build();
    }
}
