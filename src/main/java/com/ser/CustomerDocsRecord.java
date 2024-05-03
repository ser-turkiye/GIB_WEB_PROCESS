package com.ser;

import com.ser.evITAWeb.EvitaWebException;
import com.ser.evITAWeb.scripting.record.RecordScripting;

public class CustomerDocsRecord extends RecordScripting {
    @Override
    public void onInit() throws EvitaWebException {
        addArchiveScriptingForQuickFiling("*", CustomerDocsArchive.class.getName());
        super.onInit();
    }
}
