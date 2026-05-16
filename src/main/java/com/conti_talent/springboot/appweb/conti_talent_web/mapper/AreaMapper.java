package com.conti_talent.springboot.appweb.conti_talent_web.mapper;

import com.conti_talent.springboot.appweb.conti_talent_web.dto.AreaDTO;
import com.conti_talent.springboot.appweb.conti_talent_web.model.Area;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AreaMapper {

    public AreaDTO toDTO(Area area) {
        if (area == null) return null;
        return new AreaDTO(area.getId(), area.getNombre(),
                area.getDescripcion(), area.getIcono(), area.getColor());
    }

    public Area toEntity(AreaDTO dto) {
        if (dto == null) return null;
        Area area = new Area(dto.getNombre(), dto.getDescripcion(), dto.getIcono(), dto.getColor());
        area.setId(dto.getId());
        return area;
    }

    public List<AreaDTO> toDTOList(List<Area> areas) {
        if (areas == null) return List.of();
        return areas.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
