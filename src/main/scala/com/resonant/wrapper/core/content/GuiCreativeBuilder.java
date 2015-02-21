package com.resonant.wrapper.core.content;

import net.minecraft.client.gui.GuiTextField;
import nova.core.gui.ComponentEvent;
import nova.core.gui.Gui;
import nova.core.gui.components.Button;
import nova.core.gui.layout.Anchor;

public class GuiCreativeBuilder extends Gui {
	private GuiTextField textFieldSize;

	public GuiCreativeBuilder() {
		super("creativeBuilder");
		//TODO: Add text field
		addElement(new Button("build", "I'm EAST")
				.setMaximumSize(Integer.MAX_VALUE, 120)
				.registerEventListener((event) -> System.out.println("Build"), ComponentEvent.ActionEvent.class),
			Anchor.SOUTH);

		//TODO: Add build button
	}
}