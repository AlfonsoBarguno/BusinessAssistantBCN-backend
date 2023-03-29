package com.businessassistantbcn.gencat.helper;

import com.businessassistantbcn.gencat.dto.output.RaiscResponseDto;
import com.businessassistantbcn.gencat.exception.ExpectedJSONFieldNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

@Component
public class RaiscDeserializer {

    public List<RaiscResponseDto> deserialize(Object data) {

        List<RaiscResponseDto> listRaisc = new ArrayList<>();

        LinkedHashMap<?, ?> inputData = Optional.ofNullable(data)
                .filter(LinkedHashMap.class::isInstance)
                .map(LinkedHashMap.class::cast)
                .orElseThrow(() -> new ExpectedJSONFieldNotFoundException("The object must be an instance of LinkedHashMap"));

        ArrayList<?> dataAdsArray = Optional.ofNullable(inputData.get("data"))
                .map(ArrayList.class::cast)
                .orElseThrow(() -> new ExpectedJSONFieldNotFoundException("Field 'data' was not found"));

        //Get field names from metadata
        Map<Object, Integer> fieldNames = metaDataReader(inputData);

        dataAdsArray.forEach(object -> {
            ArrayList<?> element = Optional.of(object)
                    .map(ArrayList.class::cast)
                    .orElseGet(ArrayList::new);
            try {
                RaiscResponseDto raiscResponseDto = new RaiscResponseDto(
                        element.get(fieldNames.get(":id")).toString(),//idRaisc
                        element.get(fieldNames.get("entitat_oo_aa_o_departament_1")).toString(),//entity
                        element.get(fieldNames.get("tipus_de_convocat_ria")).toString(),//raiscType
                        (String) element.get(fieldNames.get("any_de_la_convocat_ria")),//year
                        element.get(fieldNames.get("t_tol_convocat_ria_catal")).toString(),//title_CA
                        element.get(fieldNames.get("t_tol_convocat_ria_castell")).toString(),//title_ES
                        element.get(fieldNames.get("url_catala_bases_reg")).toString(),//bases_CA
                        element.get(fieldNames.get("url_castella_bases_reguladores")).toString(),//bases_ES
                        element.get(fieldNames.get("tipus_d_instument_ajut")).toString(),//subventionType
                        element.get(fieldNames.get("codi_regio_apli")).toString(),//idRegion
                        element.get(fieldNames.get("regio_apli")).toString(),//region
                        element.get(fieldNames.get("codi_finalitat_publica")).toString(),//idScope
                        element.get(fieldNames.get("finalitat_publica")).toString(),//scope
                        element.get(fieldNames.get("codi_sector_eco")).toString(),//idSector
                        element.get(fieldNames.get("sector_eco_afectat")).toString(),//sector
                        (String) element.get(fieldNames.get("origen_del_finan_ament")),//origin
                        element.get(fieldNames.get("import_finan_ament_sec_pub")).toString(),//maxBudgetPublic
                        element.get(fieldNames.get("import_finan_ament_ue")).toString(),//maxBudgetUE
                        element.get(fieldNames.get("import_total_convocat_ria")).toString(),//maxBudget
                        element.get(fieldNames.get("data_inici_presentaci_sol")).toString(),//startDate
                        (String) element.get(fieldNames.get("data_fi_presentaci_sol")),//endDate
                        (String) element.get(fieldNames.get("seu_electr_nica")),//urlRequest
                        element.get(fieldNames.get("objecte_de_la_convocat_ria")).toString());//description
                listRaisc.add(raiscResponseDto);
            } catch (NullPointerException npe) {
                throw new ExpectedJSONFieldNotFoundException("JSON field not found");
            }
        });

        return listRaisc;

    }


    private Map<Object, Integer> metaDataReader(LinkedHashMap<?, ?> inputData) {
        //Returns map with id of each field name and its position (index)

        HashMap<?, ?> metaDataWrapped = Optional.ofNullable(inputData.get("meta")).map(HashMap.class::cast)
                .orElseThrow(() -> new ExpectedJSONFieldNotFoundException("Field 'meta' was not found"));

        HashMap<?, ?> metaData = Optional.ofNullable(metaDataWrapped.get("view")).map(HashMap.class::cast)
                .orElseThrow(() -> new ExpectedJSONFieldNotFoundException("Field 'view' was not found"));

        ArrayList<?> columns = Optional.ofNullable(metaData.get("columns")).map(ArrayList.class::cast)
                .orElseThrow(() -> new ExpectedJSONFieldNotFoundException("Field 'columns' was not found"));

        columns.forEach(column -> Optional.of(column).map(HashMap.class::cast).orElseGet(HashMap::new));

        Map<Object, Integer> fields = new HashMap<>();
        IntStream.range(0, columns.size()).forEach(index ->
                fields.put(
                        Optional.of(columns.get(index))
                                .map(HashMap.class::cast)
                                .orElseGet(HashMap::new)
                                .get("fieldName"),
                        index));

        return fields;
    }
}