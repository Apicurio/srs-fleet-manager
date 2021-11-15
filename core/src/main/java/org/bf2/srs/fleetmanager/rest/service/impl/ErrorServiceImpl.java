package org.bf2.srs.fleetmanager.rest.service.impl;

import org.bf2.srs.fleetmanager.common.errors.UserErrorCode;
import org.bf2.srs.fleetmanager.common.operation.auditing.Audited;
import org.bf2.srs.fleetmanager.rest.service.ErrorNotFoundException;
import org.bf2.srs.fleetmanager.rest.service.ErrorService;
import org.bf2.srs.fleetmanager.rest.service.model.ErrorDto;
import org.bf2.srs.fleetmanager.rest.service.model.ErrorListDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;

import static org.bf2.srs.fleetmanager.common.operation.auditing.AuditingConstants.KEY_USER_ERROR_ID;

/**
 * @author Jakub Senko <jsenko@redhat.com>
 */
@ApplicationScoped
public class ErrorServiceImpl implements ErrorService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Audited
    public ErrorListDto getErrors(Integer page, Integer size) {
        page = (page != null) ? page : 1;
        size = (size != null) ? size : 10;
        log.debug("Loading errors. page='{}' size='{}'", page, size);
        log.debug("subMap: {}", UserErrorCode.getValueMap().subMap((page - 1) * size + 1, page + size).values());
        var items = UserErrorCode.getValueMap().subMap((page - 1) * size + 1, page + size).values().stream().map(
                uec -> ErrorDto.builder()
                        .id(Integer.toString(uec.getId()))
                        .code(uec.getCode())
                        .reason(genericReason(uec.getReason(), uec.getReasonArgsCount()))
                        .operationId("")
                        .build())
                .collect(Collectors.toList());
        return ErrorListDto.builder()
                .page(page)
                .size(size)
                .total((long) UserErrorCode.getValueMap().size())
                .items(items)
                .build();
    }

    @Override
    @Audited(extractParameters = {"0", KEY_USER_ERROR_ID})
    public ErrorDto getError(int id) throws ErrorNotFoundException {
        return Optional.ofNullable(UserErrorCode.getValueMap().get(id)).map(
                uec -> ErrorDto.builder()
                        .id(Integer.toString(uec.getId()))
                        .code(uec.getCode())
                        .reason(genericReason(uec.getReason(), uec.getReasonArgsCount()))
                        .operationId("")
                        .build())
                .orElseThrow(() -> new ErrorNotFoundException(Integer.toString(id)));
    }

    private static String genericReason(String reason, int argCount) {
        return String.format(reason, Stream.generate(() -> "?").limit(argCount).toArray());
    }
}
