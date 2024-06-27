package com.ser;

import com.ser.evITAWeb.EvitaWebException;
import com.ser.evITAWeb.scripting.filing.FilingEnvironmentScripting;

public class RetailDocsFiling extends FilingEnvironmentScripting {
    @Override
    public void onInit() throws EvitaWebException {
        log.info("GIB..WEB CUBE..RETAIL FILING STARING...");
        this.addArchiveScripting("*", CustomerDocsArchive.class.getName());

    }
}
