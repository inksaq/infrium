package com.infrium.core.gui;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuiItem {

  private final ItemBuilder itemBuilder;
  private int slot;
  private ItemClick onItemClick;

  public GuiItem(int slot, ItemBuilder builder, ItemClick click) {
    this.slot = slot;
    this.onItemClick = click;
    this.itemBuilder = builder;
  }
}
