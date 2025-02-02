/**
 *
 * Copyright (C) 2002-2012 "SYSNET International, Inc."
 * support@sysnetint.com [http://www.sysnetint.com]
 *
 * This file is part of OpenEMPI.
 *
 * OpenEMPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openhie.openempi.openpixpdq.v3;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;

/**
 * This class was generated by Apache CXF 2.7.0
 * 2012-11-21T11:21:40.005-05:00
 * Generated source version: 2.7.0
 * 
 */
@WebService(targetNamespace = "urn:org:openhie:openpixpdq:services", name = "PDQSupplier_PortType")
@XmlSeeAlso({org.hl7.v3.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface PDQSupplierPortType {

    @WebResult(name = "PRPA_IN201306UV02", targetNamespace = "urn:hl7-org:v3", partName = "Body")
    @Action(input = "urn:hl7-org:v3:QUQI_IN000003UV01_Continue", output = "urn:hl7-org:v3:PRPA_IN201306UV02")
    @WebMethod(operationName = "PDQSupplier_QUQI_IN000003UV01_Continue", action = "urn:hl7-org:v3:QUQI_IN000003UV01_Continue")
    org.hl7.v3.PRPAIN201306UV02 pdqSupplierQUQIIN000003UV01Continue(
            @WebParam(partName = "Body", name = "QUQI_IN000003UV01", targetNamespace = "urn:hl7-org:v3")
                    org.hl7.v3.QUQIIN000003UV01Type body
    );

    @WebResult(name = "MCCI_IN000002UV01", targetNamespace = "urn:hl7-org:v3", partName = "Body")
    @Action(input = "urn:hl7-org:v3:QUQI_IN000003UV01_Cancel", output = "urn:hl7-org:v3:MCCI_IN000002UV01")
    @WebMethod(operationName = "PDQSupplier_QUQI_IN000003UV01_Cancel", action = "urn:hl7-org:v3:QUQI_IN000003UV01_Cancel")
    org.hl7.v3.MCCIIN000002UV01 pdqSupplierQUQIIN000003UV01Cancel(
            @WebParam(partName = "Body", name = "QUQI_IN000003UV01_Cancel", targetNamespace = "urn:hl7-org:v3")
                    org.hl7.v3.QUQIIN000003UV01Type body
    );

    @WebResult(name = "PRPA_IN201306UV02", targetNamespace = "urn:hl7-org:v3", partName = "Body")
    @Action(input = "urn:hl7-org:v3:PRPA_IN201305UV02", output = "urn:hl7-org:v3:PRPA_IN201306UV02")
    @WebMethod(operationName = "PDQSupplier_PRPA_IN201305UV02", action = "urn:hl7-org:v3:PRPA_IN201305UV02")
    org.hl7.v3.PRPAIN201306UV02 pdqSupplierPRPAIN201305UV02(
            @WebParam(partName = "Body", name = "PRPA_IN201305UV02", targetNamespace = "urn:hl7-org:v3")
                    org.hl7.v3.PRPAIN201305UV02 body
    );
}
