package com.example.sample.global.common.mapper;

import java.util.List;


/**
 * EntityMapper
 *
 * @author : hhh
 * @version 1.0
 * @date : 1/31/26
 */
public interface EntityMapper<D, E> {
    E toEntity(D domain);
    D toDomain(E entity);
    List<D> toDomainList(List<E> entityList);
    List<E> toEntityList(List<D> domainList);
}
