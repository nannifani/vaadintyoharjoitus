package com.example.application.views;

import com.example.application.data.Event;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

public class EventForm extends FormLayout {

    private final TextField title = new TextField("Title");
    private final TextField organizer = new TextField("Organizer");
    private final TextField contactPhone = new TextField("Phone");
    private final DatePicker eventDate = new DatePicker("Date");

    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button cancel = new Button("Cancel");

    private final Binder<Event> binder = new BeanValidationBinder<>(Event.class);
    private Event event;

    public EventForm() {
        add(title, organizer, contactPhone, eventDate, createButtonsLayout());
        binder.bindInstanceFields(this);

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);
    }

    public void setEvent(Event event) {
        this.event = event;
        binder.readBean(event);
    }

    public interface SaveListener {
        void onSave(Event event);
    }

    public interface DeleteListener {
        void onDelete(Event event);
    }

    public interface CancelListener {
        void onCancel();
    }

    private SaveListener saveListener;
    private DeleteListener deleteListener;
    private CancelListener cancelListener;

    public void setSaveListener(SaveListener listener) {
        this.saveListener = listener;
    }

    public void setDeleteListener(DeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setCancelListener(CancelListener listener) {
        this.cancelListener = listener;
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(e -> {
            if (binder.writeBeanIfValid(event)) {
                saveListener.onSave(event);
            }
        });
        delete.addClickListener(e -> deleteListener.onDelete(event));
        cancel.addClickListener(e -> cancelListener.onCancel());

        return new HorizontalLayout(save, delete, cancel);
    }
}
