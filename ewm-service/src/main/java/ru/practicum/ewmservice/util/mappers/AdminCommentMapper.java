package ru.practicum.ewmservice.util.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmservice.event.dto.EventIncomeDto;
import ru.practicum.ewmservice.event.dto.StateActions;
import ru.practicum.ewmservice.event.model.AdminComment;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class AdminCommentMapper {

    public static AdminComment toAdminComment(Long eventId, EventIncomeDto dto) {
        AdminComment adminComment = new AdminComment();

        adminComment.setText(dto.getComment());
        adminComment.setEventId(eventId);

        return adminComment;
    }

    public static List<AdminComment> toAdminComment(List<EventIncomeDto> dto) {
        return dto.stream()
                .filter(d -> d.getStateAction() != null)
                .filter(d -> d.getStateAction().equals(StateActions.REJECT_EVENT))
                .filter(d -> d.getComment() != null && !d.getComment().isBlank())
                .map(d -> toAdminComment(d.getEventId(), d))
                .collect(Collectors.toList());
    }
}
