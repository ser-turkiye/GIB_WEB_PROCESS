package com.ser;

import java.util.Vector;

import com.ser.blueline.IDocument;
import com.ser.blueline.IInformationObject;
import com.ser.evITAWeb.EvitaWebException;
import com.ser.evITAWeb.api.IDialog;
import com.ser.evITAWeb.api.actions.IBasicAction;
import com.ser.evITAWeb.api.actions.IMessageAction;
import com.ser.evITAWeb.api.actions.IStopFurtherAction;
import com.ser.evITAWeb.api.archive.IArchiveDlg;
import com.ser.evITAWeb.api.context.IFolderContext;
import com.ser.evITAWeb.api.context.IScriptingContext;
import com.ser.evITAWeb.api.context.ISourceContext;
import com.ser.evITAWeb.api.controls.IControl;
import com.ser.evITAWeb.api.controls.IMultiLineEdit;
import com.ser.evITAWeb.api.controls.ISelectionBox;
import com.ser.evITAWeb.api.controls.ITextField;
import com.ser.evITAWeb.scripting.Doxis4ClassFactory;
import com.ser.evITAWeb.scripting.archive.ArchiveScripting;
import com.ser.foldermanager.INode;
import utils.Utils;

/**
 * Example class that demonstrates control scripting for selection boxes, buttons, text boxes and date fields. Requires an archive dialog with the following controls:<BR>
 * <UL>
 * <LI>selection boxes named: src1, dest1, dest2</LI>
 * <LI>a button named: src2</LI>
 * <LI>a category tree control named: cattree1</LI>
 * <LI>a DB Record selector named: dbrs1</LI>
 * <LI>a checkbox named: disableall</LI>
 * <LI>text boxes named: Desk1, Desk2, Desk3</LI>
 * <LI>a button named: save</LI>
 * <LI>a date field named: demoDate</LI>
 * </UL>
 * The selection box "src1" is initialized with the values "action_item_1", "action_item_2", "else_action_item". The remaining text boxes and selection boxes are empty on init. <BR>
 * <A href=FilingDocumentMetadataControlScriptingSample.java.html target=_blank>Display Java source code in separate window.</A><BR>
 * <B>Note</B> Javascript should no longer be used to program controls. However, existing scripts will still be executed as long as there is no Java code for the same control. If both Javascript and
 * Java code exists, the Java code will be executed.
 **/
public class CustomerDocsArchive extends ArchiveScripting {
    private IDialog dlg;

    public IInformationObject getSourceInformationObject(ISourceContext sourceContext){
        log.info("GIB WEB CUBE----Getting Source Info Object");
        IScriptingContext source = sourceContext.getSourceContext();
        if(source == null){
            log.info("Didnt come from any source");
            return null;
        }
        if((source instanceof IFolderContext)){
            log.info("Came from a folder");
            IFolderContext folderContext = (IFolderContext) source;
            return folderContext.getFolder();
        }else{
            log.info("Didnt come from a folder");
            return null;
        }
    }
    public INode getSourceNode(ISourceContext sourceContext){
        IFolderContext folderContext = (IFolderContext) sourceContext.getSourceContext();
        return folderContext.getNode();
    }
    @Override
    public IBasicAction onDescriptorValuesSave() throws EvitaWebException {
        IArchiveDlg archivedlg = this.getDialog();
        IControl typeField = archivedlg.getFieldByName("DocumentType");
        String cat = "";
        String value = "";
        String mandFields = "";
        String[] mandatoryDescs = new String[0];
        try {
            cat = Utils.getCategoryNameFromGVlist(getDoxisServer(),archivedlg.getId());
        } catch (Exception e) {
            log.error("bbb hata:::", e);
            throw new EvitaWebException(e.getMessage());
        }
        log.info("VALIDATION DIALOG: " + archivedlg.getId());
        log.info("VALIDATION CATEGORY: " + cat);
        log.info("VALIDATION FIELD NAME222: " + typeField);

        if (typeField != null && typeField instanceof ISelectionBox) {
            ISelectionBox selectionBox = (ISelectionBox) typeField;
            String typeValue = selectionBox.getSelectedItem();
            log.info("VALIDATION TYPE FIELD VALUE: " + typeValue);
            try {
                mandFields = Utils.getMandatoryFromGVlist(getDoxisServer(),cat,typeValue,log);
            } catch (Exception e) {
                log.error("bbb hata:::", e);
                throw new EvitaWebException(e.getMessage());
            }

            log.info("VALIDATION MAND FIELDS FROM GV: " + mandFields);
            mandatoryDescs = mandFields.split(",");
            log.info("VALIDATION MAND DESCSS FINAL: " + mandatoryDescs);
            if(!mandFields.isEmpty()) {
                for (String dName : mandatoryDescs) {
                    log.info("VALIDATION CHECK MAND DESC NAME: " + dName);
                    IControl checkField = archivedlg.getFieldByName(dName);
                    if (checkField != null && checkField instanceof ITextField) {
                        ITextField textField = (ITextField) checkField;
                        value = textField.getText();
                        log.info("VALIDATION CHECK MAND DESC VALUE: " + value);
                    }
                    if (value == null || "".equals(value)) {
                        IStopFurtherAction createStopFurtherAction = Doxis4ClassFactory.createStopFurtherAction();
                        createStopFurtherAction.setMessage("Please fill in the " + checkField.getName() + "");
                        createStopFurtherAction.setType(IMessageAction.EnumMessageType.ERROR);
                        return createStopFurtherAction;
                    }
                }
            }
        }
//        if (true) {
//            IStopFurtherAction createStopFurtherAction = Doxis4ClassFactory.createStopFurtherAction();
//            createStopFurtherAction.setMessage("Files above 1 MB are not permitted. Try again after cleanup!!!");
//            createStopFurtherAction.setType(IMessageAction.EnumMessageType.ERROR);
//            return createStopFurtherAction;
//        } else {
//            IMessageAction msg = Doxis4ClassFactory.createShowMessageAction();
//            msg.setMessage("Descriptors saved!");
//            msg.setCaption("Done");
//            msg.setType(IMessageAction.EnumMessageType.INFO);
//            return msg;
//        }
        return null;
    }
    @Override
    public void onInit() throws EvitaWebException {
        try {
            log.info("GIB WEB CUBE----Archive OnInit Start.......");
            this.dlg = getDialog();
            IInformationObject parentFolder = getSourceInformationObject(getSourceContext());
            if(parentFolder == null) return;
            INode sourceNode = getSourceNode(getSourceContext());
            log.info("Came from folder name:" + parentFolder.getDisplayName());
            log.info("Came from folder ID:" + parentFolder.getID());
            log.info("Came from source node:" + sourceNode.getName());
            log.info("GIB WEB CUBE----Parent Folder..display name....." + parentFolder.getDisplayName());
            Vector<IControl> fields = dlg.getFields();
            for(IControl ctrl : fields){
                String descriptorId = ctrl.getDescriptorId();
                if(descriptorId == null) continue;
                if(descriptorId.equals("0644eb7c-1280-447c-923e-3856c7dfb94e")) continue;
                String descriptorValue = parentFolder.getDescriptorValue(descriptorId);
                log.info("DESC.ID:" + descriptorId);
                log.info("DESC.VALUE:" + descriptorValue);
                if(!(ctrl instanceof ITextField)) continue;
                if(descriptorValue != null){
                    ((ITextField) ctrl).setText(descriptorValue);
                    ctrl.setReadonly(true);
                }
            }
            super.onInit();
        } catch (RuntimeException e) {
            log.error("bbb hata:::", e);
            throw new EvitaWebException(e.getMessage());
        }
    }

    private  ITextField getTextFieldFromDlg(String textFieldName){
        try{
            return dlg.getTextField(textFieldName);
        }catch (Exception e){
            log.error("Exception, couldnt get textfield: " + textFieldName);
            return null;
        }
    }

}

