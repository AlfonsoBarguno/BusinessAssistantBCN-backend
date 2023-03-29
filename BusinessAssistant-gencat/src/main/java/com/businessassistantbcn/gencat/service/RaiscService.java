package com.businessassistantbcn.gencat.service;

import com.businessassistantbcn.gencat.config.PropertiesConfig;
import com.businessassistantbcn.gencat.dto.GenericResultDto;
import com.businessassistantbcn.gencat.dto.output.RaiscResponseDto;
import com.businessassistantbcn.gencat.dto.output.ResponseScopeDto;
import com.businessassistantbcn.gencat.helper.JsonHelper;
import com.businessassistantbcn.gencat.helper.RaiscDeserializer;
import com.businessassistantbcn.gencat.proxy.HttpProxy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class RaiscService {
    @Autowired
    private GenericResultDto<RaiscResponseDto> genericResultDto;
    @Autowired
    private GenericResultDto<ResponseScopeDto> genericScopeDto;
    @Autowired
    private RaiscResponseDto raiscResponseDto;
    @Autowired
    private PropertiesConfig config;
    @Autowired
    HttpProxy httpProxy;
    @Autowired
    RaiscDeserializer raiscDeserializer;

    public Mono<GenericResultDto<RaiscResponseDto>> getPageByRaiscYear(int offset, int limit, String year) throws MalformedURLException {
        return getData().flatMap(raiscResponseDtos -> {RaiscResponseDto[] pageResult = JsonHelper.filterDto(raiscResponseDtos, offset, limit);
            genericResultDto.setInfo(offset, limit, pageResult.length, pageResult);
            return Mono.just(genericResultDto);
        });
    }

    private Mono<RaiscResponseDto[]> getData() throws MalformedURLException {
        return httpProxy.getRequestData(new URL(config.getDs_raisc()), Object.class)
                .flatMap(raiscResponseDto -> {
                    RaiscResponseDto[] responses = raiscDeserializer.deserialize(raiscResponseDto)
                            .toArray(RaiscResponseDto[]::new);
                    return Mono.just(responses);
                });
    }

    private Mono<GenericResultDto<RaiscResponseDto>> getRaiscDefaultPage() {
        genericResultDto.setInfo(0, 0, 0, new RaiscResponseDto[0]);
        return Mono.just(genericResultDto);
    }
    public Mono<List<ResponseScopeDto>> getScopes(int offset, int limit) throws MalformedURLException {
        // Hacer un GET a la URL que se encuentra en el application.yml
        Mono<String> response =  httpProxy.getRequestData(new URL(config.getDs_scopes()), String.class);
        // Extraer ResponseScopeDto de toda la data de un Flux.
        // .skip para saltar la data del offset y .take para limitar los resultados
        return response
                .map(content -> extractScopes(content))
                .flatMapMany(Flux::fromIterable)
                .distinct()
                .collectList()
                .map(scopes -> JsonHelper.filterDto(scopes.toArray(new ResponseScopeDto[scopes.size()]), offset, limit))
                .flatMapMany(Flux::fromArray)
                .collectList();
    }

    private static List<ResponseScopeDto> extractScopes(String content) {
        List<ResponseScopeDto> scopes = new ArrayList<>();
        // crear un Json de la data extraida
        org.json.JSONObject json = new JSONObject(content);
        JSONArray data = json.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONArray row = data.getJSONArray(i);
            // En este caso sé que es el 35 y 36 porque lo pone en RaiscResponseDto
            // que sería la data entera
            String idScope = row.getString(35);
            String scope = row.getString(36);
            scopes.add(new ResponseScopeDto(idScope, scope));
        }
        return scopes;
    }
}
