/*
 * Copyright (c) 2006, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.governance.wsdltool.ui.clients;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.governance.wsdltool.stub.WSDLToolServiceExceptionException;
import org.wso2.carbon.governance.wsdltool.stub.WSDLToolServiceStub;
import org.wso2.carbon.governance.wsdltool.stub.beans.xsd.ServiceInfoBean;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.ui.CarbonUIUtil;
import org.wso2.carbon.utils.ServerConstants;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;
import java.rmi.RemoteException;

public class WSDLToolServiceClient {

    private static final Log log = LogFactory.getLog(WSDLToolServiceClient.class);

    private WSDLToolServiceStub stub;
    private String epr;

    public WSDLToolServiceClient(ConfigurationContext configContext, String backendServerURL, String cookie)
            throws RegistryException {
        epr = backendServerURL + "WSDLToolService";
        try {
            stub = new WSDLToolServiceStub(configContext, epr);
            ServiceClient client = stub._getServiceClient();
            Options option = client.getOptions();
            option.setManageSession(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
        } catch (AxisFault axisFault) {
            String msg = "Failed to initiate wsdltool service client. " + axisFault.getMessage();
            log.error(msg, axisFault);
            throw new RegistryException(msg, axisFault);
        }
    }

    public WSDLToolServiceClient(ServletConfig config, HttpSession session)
            throws RegistryException {

        String cookie = (String)session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
        String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
        ConfigurationContext configContext = (ConfigurationContext) config.
                getServletContext().getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
        epr = backendServerURL + "WSDLToolService";

        try {
            stub = new WSDLToolServiceStub(configContext, epr);

            ServiceClient client = stub._getServiceClient();
            Options option = client.getOptions();
            option.setManageSession(true);
            option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);

        } catch (AxisFault axisFault) {
            String msg = "Failed to initiate wsdltool service client. " + axisFault.getMessage();
            log.error(msg, axisFault);
            throw new RegistryException(msg, axisFault);
        }
    }

    public void addMEXService(String path, ServiceInfoBean serviceInfo) throws Exception {
        stub.addMEXService(path, serviceInfo);
    }

    /**
     * Method for get membrane diff
     * @param resource1Path registry path of resource1
     * @param resource2Path registry path of resource2
     * @param type result type
     * @return String[] membrane result diff array
     * @throws RegistryException
     */
    public String[] getMembraneDiffArrayResult(String resource1Path, String resource2Path, String type)
            throws RegistryException {
        try {
            // Get membrane diff array
            return stub.getMembraneDiffArrayResult(resource1Path, resource2Path, type);
        } catch (RemoteException e) {
            String msg = "Backend Service is unavailable";
            throw new RegistryException(msg, e);
        } catch (WSDLToolServiceExceptionException e) {
            String msg = "Cannot get the Membrane descriptive diff";
            throw new RegistryException(msg, e);
        }
    }

    /**
     * Method to get diff view type
     * @param resourcePath1 registry path of resource1
     * @param resourcePath2 registry path of resource2
     * @return String diff type string
     * @throws RegistryException
     */
    public String getDiffViewType(String resourcePath1, String resourcePath2)
            throws RegistryException {
        try {
            // Get type of the diff view
            return stub.getDiffViewType(resourcePath1, resourcePath2);
        } catch (RemoteException e) {
            String msg = "Backend Service is unavailable";
            throw new RegistryException(msg, e);
        } catch (Exception e) {
            String msg = "Backend Service is unavailable";
            throw new RegistryException(msg, e);
        }
    }
}
