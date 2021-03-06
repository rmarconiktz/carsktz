package com.kaitzen

import grails.transaction.Transactional

@Transactional
class OwnerService {

    Owner addOwner(Owner owner) {
        if (owner.hasErrors()) {
            throw new OwnerException(
                    message: "Invalid or empty owner", owner: owner)
        }
        else {
            owner.save(failOnError: true)
            return owner
        }
    }

    def searchOwner(params) {
        def query = (params.type?.toUpperCase() == 'INCLUDE' ? QueryTypeEnum.INCLUDE : QueryTypeEnum.EXCLUDE).getQuery(params)

        def criteria = Owner.createCriteria()
        def ownerList = criteria.list(query, max: 10)

        return ownerList
    }

    enum QueryTypeEnum {
        INCLUDE,
        EXCLUDE

        def getQuery(params) {
            def query
            switch (this) {
                case INCLUDE: query = {
                        or {
                            if (params.nombre)
                                like("nombre", '%' + params.nombre + '%')
                            if (params.apellido)
                                like("apellido", '%' + params.apellido + '%')
                            if (params.nacionalidad)
                                like("nacionalidad", '%' + params.nacionalidad + '%')
                            if (params.dni) {
                                def dniIni = params.dni.padRight(8, '0').toInteger()
                                def dniEnd = params.dni.padRight(8, '0').toInteger() + 10**(8 - params.dni.length()) - 1
                                between("dni", dniIni, dniEnd)
                            }
                        }
                    }
                    break
                default: query = {
                        and {
                            if (params.nombre)
                                like("nombre", '%' + params.nombre + '%')
                            if (params.apellido)
                                like("apellido", '%' + params.apellido + '%')
                            if (params.nacionalidad)
                                like("nacionalidad", '%' + params.nacionalidad + '%')
                            if (params.dni) {
                                def dniIni = params.dni.padRight(8, '0').toInteger()
                                def dniEnd = params.dni.padRight(8, '0').toInteger() + 10**(8 - params.dni.length()) - 1
                                between("dni", dniIni, dniEnd)
                            }
                        }
                    }
            }
            return query
        }
    }
}

class OwnerException extends RuntimeException{ //Forces transaction to roll back if exception occur
    String message
    Car owner
}
