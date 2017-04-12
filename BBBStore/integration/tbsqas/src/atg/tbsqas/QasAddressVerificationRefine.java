package atg.tbsqas;

import java.net.URLDecoder;

import atg.nucleus.logging.ApplicationLogging;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class QasAddressVerificationRefine extends DynamoServlet {

    public QasAddressVerificationRefine() {}

    protected String onDemandUrl = "";
    protected ApplicationLogging atgLog = null;
    
    public String getOnDemandUrl() {
        return onDemandUrl;
    }
    public void setOnDemandUrl(String onDemandUrl) {
        this.onDemandUrl = onDemandUrl;
    }
    public ApplicationLogging getAtgLog() {
        return atgLog;
    }
    public void setAtgLog(ApplicationLogging atgLog) {
        this.atgLog = atgLog;
    }



    @Override
    public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response) throws java.io.IOException, javax.servlet.ServletException {

        try {
            String addlayout = URLDecoder.decode(request.getParameter("addlayout"), "UTF-8");
            String moniker = URLDecoder.decode(request.getParameter("moniker"), "UTF-8");
            String refineText = URLDecoder.decode(request.getParameter("refinetext"), "UTF-8");

            QasAddressVerification qas = new QasAddressVerification();
            qas.setOnDemandUrl(getOnDemandUrl());

            qas. setOnDemandLayout(addlayout);

            qas.doRefine(request, response, moniker, refineText);

            request.setParameter("addresslines", qas.getAddressLines());
            request.setParameter("verificationlevel", qas.getVerificationLevel());
            request.setParameter("dpvstatus", qas.getDpvStatus());
            request.setParameter("picklistitems", qas.getPicklistItems());
            request.setParameter("fullmoniker", qas.getFullMoniker());

        } catch(Exception ex) {
            ApplicationLogging  log = this.getAtgLog();
            if(log != null) {
                if(log.isLoggingError()) {
                    log.logDebug(ex);
                }
            }
        }
        request.serviceLocalParameter("output", request, response);
    }
}
