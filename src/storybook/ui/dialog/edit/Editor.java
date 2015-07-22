/*
 * SbApp: Open Source software for novelists and authors.
 * Original idea 2008 - 2012 Martin Mustun
 * Copyrigth (C) Favdb
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package storybook.ui.dialog.edit;

import java.awt.CardLayout;
import java.awt.Color;
import storybook.model.hbn.entity.AbstractEntity;
import storybook.model.hbn.entity.Chapter;
import storybook.model.hbn.entity.Gender;
import storybook.model.hbn.entity.Idea;
import storybook.model.hbn.entity.Item;
import storybook.model.hbn.entity.ItemLink;
import storybook.model.hbn.entity.Location;
import storybook.model.hbn.entity.Part;
import storybook.model.hbn.entity.Person;
import storybook.model.hbn.entity.Scene;
import storybook.model.hbn.entity.Strand;
import storybook.model.hbn.entity.Tag;
import storybook.model.hbn.entity.TagLink;
import storybook.toolkit.I18N;
import storybook.ui.MainFrame;

/**
 *
 * @author favdb
 */
public class Editor extends javax.swing.JPanel {

	//EditorDlg editorDlg;
	MainFrame mainFrame;
	String currentView = "NONE";
	String[] typeEdit = {
		"NONE", "Strand", "Part", "Chapter", "Scene", "Person", "Relationship",
		"Location", "Item", "ItemLink", "Tag", "TagLink", "Idea", "Gender"
	};
	private final CardLayout card = new CardLayout(0, 0);
	EditBlank editBlank;
	EditStrand editStrand;
	EditPart editPart;
	EditChapter editChapter;
	EditScene editScene;
	EditPerson editPerson;
	EditLocation editLocation;
	EditItem editItem;
	EditItemLinks editItemLinks;
	EditTag editTag;
	EditTagLinks editTagLinks;
	EditIdea editIdea;
	EditGender editGender;

	/**
	 * Creates new form Editor
	 *
	 * @param m (MainFrame)
	 */
	public Editor(MainFrame m) {
		initComponents();
		mainFrame = m;
		editBlank = new EditBlank();
		editPart = new EditPart(this, null);
		editChapter = new EditChapter(this, null);
		editScene = new EditScene(this, null);
		editPerson = new EditPerson(this, null);
		editLocation = new EditLocation(this, null);
		editItem = new EditItem(this, null);
		editItemLinks = new EditItemLinks(this, null);
		editTag = new EditTag(this, null);
		editTagLinks = new EditTagLinks(this, null);
		editIdea = new EditIdea(this, null);
		editGender = new EditGender(this, null);
		editorPane.setLayout(card);
		currentView = "NONE";
		card.show(editorPane, currentView);
	}

	/*public Editor(EditorDlg dlg, MainFrame m, AbstractEntity obj) {
		SbApp.trace("Editor(EditorDlg, MainFrame, Object)");
		initComponents();
		editorDlg=dlg;
		mainFrame = m;
		editorPane.setLayout(card);
		currentView = "NONE";
		setEdit(obj);
		card.show(editorPane, currentView);
	}*/

	public final void setEdit(AbstractEntity obj) {
		String v=obj.getClass().getName();
		v=v.substring(v.lastIndexOf(".")+1);
		if (currentView.equals(v))
			return;
		// TODO save before change
		if (isModified()) {
			// TODO interdiction Edit si modification en cours
			return;
		}
		if (editorPane.getComponentCount() > 0)
			editorPane.removeAll();
		switch (v) {
			case "Strand":
				editStrand = new EditStrand(this, (Strand) obj);
				editorPane.add(editStrand, currentView);
				break;
			case "Part":
				editPart = new EditPart(this, (Part) obj);
				editorPane.add(editPart, currentView);
				break;
			case "Chapter":
				editChapter = new EditChapter(this, (Chapter) obj);
				editorPane.add(editChapter, currentView);
				break;
			case "Scene":
				editScene = new EditScene(this, (Scene) obj);
				editorPane.add(editScene, currentView);
				break;
			case "Person":
				editPerson = new EditPerson(this, (Person) obj);
				editorPane.add(editPerson, currentView);
				break;
			case "Location":
				editLocation = new EditLocation(this, (Location) obj);
				editorPane.add(editLocation, currentView);
				break;
			case "Item":
				editItem = new EditItem(this, (Item) obj);
				editorPane.add(editItem, currentView);
				break;
			case "ItemLink":
				editItemLinks = new EditItemLinks(this, (ItemLink) obj);
				editorPane.add(editItemLinks, currentView);
				break;
			case "Tag":
				editTag = new EditTag(this, (Tag) obj);
				editorPane.add(editTag, currentView);
				break;
			case "TagLink":
				editTagLinks = new EditTagLinks(this, (TagLink) obj);
				editorPane.add(editTagLinks, currentView);
				break;
			case "Idea":
				editIdea = new EditIdea(this, (Idea) obj);
				editorPane.add(editIdea, currentView);
				break;
			case "Gender":
				editGender = new EditGender(this, (Gender) obj);
				break;
			case "NONE":
				editorPane.add(editBlank, currentView);
		}
		currentView=v;
		card.show(editorPane, currentView);
		lbEditor.setText(I18N.getMsg("msg.common." + currentView.toLowerCase()));
	}

	public void removeEdit() {
		this.setVisible(false);
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of
	 * this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorScrollPane = new javax.swing.JScrollPane();
        editorPane = new javax.swing.JPanel();
        btSave = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        lbEditor = new javax.swing.JLabel();
        lbError = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        editorScrollPane.setBorder(null);

        editorPane.setBorder(null);

        javax.swing.GroupLayout editorPaneLayout = new javax.swing.GroupLayout(editorPane);
        editorPane.setLayout(editorPaneLayout);
        editorPaneLayout.setHorizontalGroup(
            editorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 516, Short.MAX_VALUE)
        );
        editorPaneLayout.setVerticalGroup(
            editorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 430, Short.MAX_VALUE)
        );

        editorScrollPane.setViewportView(editorPane);

        btSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/file-save.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("storybook/msg/messages"); // NOI18N
        btSave.setText(bundle.getString("msg.common.save")); // NOI18N
        btSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSaveActionPerformed(evt);
            }
        });

        btCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/storybook/resources/icons/16x16/cancel.png"))); // NOI18N
        btCancel.setText(bundle.getString("msg.common.cancel")); // NOI18N
        btCancel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelActionPerformed(evt);
            }
        });

        lbEditor.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        lbEditor.setText(bundle.getString("msg.common.none")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbError, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btSave, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(editorScrollPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbEditor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editorScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbError, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btCancel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btSave, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSaveActionPerformed
		if (isModified())
			saveData();
    }//GEN-LAST:event_btSaveActionPerformed

    private void btCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelActionPerformed
		//mainFrame.hideEditor();
//		editorDlg.dispose();
    }//GEN-LAST:event_btCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btSave;
    private javax.swing.JPanel editorPane;
    private javax.swing.JScrollPane editorScrollPane;
    private javax.swing.JLabel lbEditor;
    private javax.swing.JLabel lbError;
    // End of variables declaration//GEN-END:variables

	public void setError(String msg) {
		lbError.setText(msg);
		lbError.setForeground(Color.red);
	}

	private boolean isModified() {
		boolean rt = false;
		switch (currentView) {
			case "Strand":
				rt = editStrand.isModified();
				break;
			case "Part":
				rt = editPart.isModified();
				break;
			case "Chapter":
				rt = editChapter.isModified();
				break;
			case "Scene":
				rt = editScene.isModified();
				break;
			case "Person":
				rt = editPerson.isModified();
				break;
			case "Location":
				rt = editLocation.isModified();
				break;
			case "Item":
				rt = editIdea.isModified();
				break;
			case "ItemLink":
				rt = editItemLinks.isModified();
				break;
			case "Tag":
				rt = editTag.isModified();
				break;
			case "TagLink":
				rt = editTagLinks.isModified();
				break;
			case "Idea":
				rt = editIdea.isModified();
				break;
			case "Gender":
				rt = editGender.isModified();
				break;
		}
		return (rt);
	}

	private void saveData() {
		boolean r = isModified();
		switch (currentView) {
			case "Strand":
				editStrand.saveData();
				break;
			case "Part":
				editPart.saveData();
				break;
			case "Chapter":
				editChapter.saveData();
				break;
			case "Scene":
				editScene.saveData();
				break;
			case "Person":
				editPerson.saveData();
				break;
			case "Location":
				editLocation.saveData();
				break;
			case "Item":
				editIdea.saveData();
				break;
			case "ItemLink":
				editItemLinks.saveData();
				break;
			case "Tag":
				editTag.saveData();
				break;
			case "TagLink":
				editTagLinks.saveData();
				break;
			case "Idea":
				editIdea.saveData();
				break;
			case "Gender":
				editGender.saveData();
				break;
		}
		if (r) {
			mainFrame.refresh();
		}
	}
}
