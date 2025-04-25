package com.example.application.views;

import com.example.application.data.Event;
import com.example.application.services.EventService;
import com.example.application.views.EventForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Events")
@Route("events")
@Menu(order = 2, icon = LineAwesomeIconUrl.CALENDAR_SOLID)
@RolesAllowed("USER")
@Uses(Icon.class)
public class EventView extends Div {

    private final Grid<Event> grid = new Grid<>(Event.class, false);
    private final Filters filters;
    private final EventService eventService;

    private final EventForm form = new EventForm();
    private final Dialog formDialog = new Dialog();

    public EventView(EventService eventService) {
        this.eventService = eventService;
        setSizeFull();
        addClassNames("event-view");

        filters = new Filters(this::refreshGrid);
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters);

        Button addEventButton = new Button("Add Event", e -> openForm(new Event()));
        addEventButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(addEventButton, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private HorizontalLayout createMobileFilters() {
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    private Component createGrid() {
        grid.addColumn(Event::getTitle).setHeader("Title").setAutoWidth(true);
        grid.addColumn(Event::getOrganizer).setHeader("Organizer").setAutoWidth(true);
        grid.addColumn(event -> event.getEventDate() != null ? event.getEventDate().toString() : "").setHeader("Date").setAutoWidth(true);
        grid.addColumn(event -> event.getLocation() != null ? event.getLocation().getName() : "").setHeader("Location").setAutoWidth(true);
        grid.addColumn(Event::getContactPhone).setHeader("Phone").setAutoWidth(true);

        grid.setItems(query -> eventService.list(VaadinSpringDataHelpers.toSpringPageRequest(query), filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                openForm(event.getValue());
            }
        });

        return grid;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    private void openForm(Event event) {
        form.setEvent(event);

        form.setSaveListener(saved -> {
            eventService.save(saved);
            formDialog.close();
            refreshGrid();
        });

        form.setDeleteListener(deleted -> {
            eventService.delete(deleted.getId());
            formDialog.close();
            refreshGrid();
        });


        form.setCancelListener(() -> formDialog.close());

        formDialog.removeAll();
        formDialog.add(form);
        formDialog.open();
    }

    public static class Filters extends Div implements Specification<Event> {
        private final TextField title = new TextField("Title");
        private final TextField organizer = new TextField("Organizer");
        private final TextField phone = new TextField("Contact Phone");
        private final DatePicker startDate = new DatePicker("From Date");
        private final DatePicker endDate = new DatePicker("To Date");

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);

            title.setPlaceholder("Title of event");
            organizer.setPlaceholder("Organizer name");

            Button resetBtn = new Button("Reset", e -> {
                title.clear();
                organizer.clear();
                phone.clear();
                startDate.clear();
                endDate.clear();
                onSearch.run();
            });
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            Button searchBtn = new Button("Search", e -> onSearch.run());
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(title, organizer, phone, createDateRangeFilter(), actions);
        }

        private Component createDateRangeFilter() {
            FlexLayout dateRange = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRange.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRange.addClassName(LumoUtility.Gap.XSMALL);
            return dateRange;
        }

        @Override
        public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();

            if (!title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.getValue().toLowerCase() + "%"));
            }
            if (!organizer.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("organizer")), "%" + organizer.getValue().toLowerCase() + "%"));
            }
            if (!phone.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("contactPhone")), "%" + phone.getValue().toLowerCase() + "%"));
            }
            if (startDate.getValue() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), startDate.getValue()));
            }
            if (endDate.getValue() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), endDate.getValue()));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        }
    }
}
