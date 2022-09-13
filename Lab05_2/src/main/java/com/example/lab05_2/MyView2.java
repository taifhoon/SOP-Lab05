package com.example.lab05_2;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;

@Route(value = "/index2")
public class MyView2 extends FormLayout {
    MyView2(){
        VerticalLayout v1 = new VerticalLayout();
        VerticalLayout v2 = new VerticalLayout();
        ComboBox<String> goodWord = new ComboBox<>();
        ComboBox<String> badWord = new ComboBox<>();
        Word words = new Word();
        goodWord.setItems(words.goodWords);
        badWord.setItems(words.badWords);
        TextField t1 = new TextField("Add Word");
        TextField t2 = new TextField("Add Sentence");
        TextArea t3 = new TextArea("Good Sentences");
        t3.setEnabled(false);
        TextArea t4 = new TextArea("Bad Sentences");
        t4.setEnabled(false);
        Button addGood = new Button("Add Good Word");
        Button addBad = new Button("Add Bad Word");
        Button addSen = new Button("Add Sentence");
        Button show = new Button("Show Sentence");
        v1.add(t1, addGood, addBad, goodWord, badWord);
        v2.add(t2, addSen, t3, t4, show);
        this.setResponsiveSteps(new ResponsiveStep("50em", 2));
        add(v1, v2);

        addGood.addClickListener(e -> {
            MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
            data.add("sen", t1.getValue());
            ArrayList out = WebClient.create()
                    .post()
                    .uri("http://localhost:8080/addGood")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(data))
                    .retrieve()
                    .bodyToMono(ArrayList.class)
                    .block();
            goodWord.setItems(out);
            Notification.show("Insert " + t1.getValue() + " to Good Word Lists Complete.", 5000, Notification.Position.BOTTOM_START);

        });

        addBad.addClickListener(e -> {
            MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
            data.add("sen", t1.getValue());
            ArrayList out = WebClient.create()
                    .post()
                    .uri("http://localhost:8080/addBad")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(data))
                    .retrieve()
                    .bodyToMono(ArrayList.class)
                    .block();
            badWord.setItems(out);
            Notification.show("Insert " + t1.getValue() + " to Bad Word Lists Complete.", 5000, Notification.Position.BOTTOM_START);
        });

        addSen.addClickListener(e -> {
            MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
            data.add("sen", t2.getValue());
            String out = WebClient.create()
                    .post()
                    .uri("http://localhost:8080/proof")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(data))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            Notification.show(out, 5000, Notification.Position.BOTTOM_START);
        });

        show.addClickListener(e -> {
            Sentence out = WebClient.create()
                    .get()
                    .uri("http://localhost:8080/getSentence")
                    .retrieve()
                    .bodyToMono(Sentence.class)
                    .block();
            t3.setValue(out.goodSentences.toString());
            t4.setValue(out.badSentences.toString());
        });
    }
}
