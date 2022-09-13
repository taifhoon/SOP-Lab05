package com.example.lab05_2;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class WordPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    protected Word words = new Word();

    public Word getWords() {
        return words;
    }

    public void setWords(Word words) {
        this.words = words;
    }

    @RequestMapping(value = "/addBad/{word}", method = RequestMethod.GET)
    public ArrayList<String> addBadWord(@PathVariable("word") String s){
        words.badWords.add(s);
        return words.badWords;
    }

    @RequestMapping(value = "/delBad/{word}", method = RequestMethod.GET)
    public ArrayList<String> deleteBadWord(@PathVariable("word") String s){
        words.badWords.remove(s);
        return words.badWords;
    }

    @RequestMapping(value = "/addGood/{word}", method = RequestMethod.GET)
    public ArrayList<String> addGoodWord(@PathVariable("word") String s){
        words.goodWords.add(s);
        return words.goodWords;
    }

    @RequestMapping(value = "delGood/{word}", method = RequestMethod.GET)
    public ArrayList<String> deleteGoodWord(@PathVariable("word") String s){
        words.goodWords.remove(s);
        return words.goodWords;
    }


    @RequestMapping(value = "/addBad", method = RequestMethod.POST)
    public ArrayList<String> addBadWord(@RequestBody MultiValueMap<String, String> s){
        words.badWords.add(s.toSingleValueMap().get("sen"));
        return words.badWords;
    }

    @RequestMapping(value = "/delBad", method = RequestMethod.POST)
    public ArrayList<String> deleteBadWord(@RequestBody MultiValueMap<String, String> s){
        words.badWords.remove(s.toSingleValueMap().get("sen"));
        return words.badWords;
    }

    @RequestMapping(value = "/addGood", method = RequestMethod.POST)
    public ArrayList<String> addGoodWord(@RequestBody MultiValueMap<String, String> s){
        words.goodWords.add(s.toSingleValueMap().get("sen"));
        return words.goodWords;
    }

    @RequestMapping(value = "/delGood", method = RequestMethod.POST)
    public ArrayList<String> deleteGoodWord(@RequestBody MultiValueMap<String, String> s){
        words.goodWords.remove(s.toSingleValueMap().get("sen"));
        return words.goodWords;
    }

    @RequestMapping(value = "/proof", method = RequestMethod.POST)
    public String proofSentence(@RequestBody MultiValueMap<String, String> sen){
        String s = sen.toSingleValueMap().get("sen");
        boolean good = false, bad = false;

        for (String text:words.badWords) {
            if (s.contains(text)){
                bad = true;
                break;
            }
        }

        for (String text:words.goodWords) {
            if (s.contains(text)){
                good = true;
                break;
            }
        }

        if (good && bad){
            rabbitTemplate.convertAndSend("Fanout","", s);
            return "Found Bad & Good Word";
        }
        else if (good){
            rabbitTemplate.convertAndSend("Direct","good", s);
            return "Found Good Word";
        }
        else if (bad){
            rabbitTemplate.convertAndSend("Direct","bad", s);
            return "Found Bad Word";
        }
        return "Not Found Word";
    }

    @RequestMapping(value = "/getSentence", method = RequestMethod.GET)
    public Sentence getSentence(){
        Object obj = rabbitTemplate.convertSendAndReceive("getSentence", "sen", "");
        return (Sentence) obj;
    }
}
