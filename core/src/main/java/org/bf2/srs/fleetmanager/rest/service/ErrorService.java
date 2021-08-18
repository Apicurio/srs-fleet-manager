package org.bf2.srs.fleetmanager.rest.service;

import org.bf2.srs.fleetmanager.rest.service.model.ErrorDto;
import org.bf2.srs.fleetmanager.rest.service.model.ErrorListDto;

public interface ErrorService {

    ErrorListDto getErrors(Integer page, Integer size);

    ErrorDto getError(int id) throws ErrorNotFoundException;
}
