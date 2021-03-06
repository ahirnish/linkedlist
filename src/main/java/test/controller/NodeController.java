package test.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.annotation.PostConstruct;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.*;
import org.springframework.ui.Model;

import test.persistence.NodeManager;
import test.model.Node;

@Controller
@RequestMapping(value = "/node")
public class NodeController {

    private static NodeManager nodeManager = new NodeManager();

    
    @RequestMapping(value = "/")
    public String index( Model model ) {
	model.addAttribute("nodes",nodeManager.getAllNodes());
	return "index";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> create(@RequestBody Node node) {
	String res = null;
	try {
	    res=nodeManager.addNode(node.getId(),node.getData());
	} catch (Exception ex) {
	    return new ResponseEntity<>("[\"Error Occured\"]",HttpStatus.INTERNAL_SERVER_ERROR);
	}
	if(res!=null){
	    return new ResponseEntity<>("[\"Node Created\"]",HttpStatus.OK);
	} else {
	    return new ResponseEntity<>("[\"Node Saving unsuccessful\"]",HttpStatus.OK);
	}
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public String delete(String name,int data) {
	boolean res=false;
	try {
            res=nodeManager.deleteNode(name,data);
	} catch (Exception ex) {
            return ex.getMessage();
	}
        if(res){
            return "Node successfully deleted!";
	} else {
            return "Node deletion unsuccesfull OR Trying to delete head "+name;
	}
    }

    @RequestMapping(value = "/reverse", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> reverseList(@RequestParam String name)
    {
	boolean res=false;
	try{
	    res=nodeManager.reverse(name);
	}catch (Exception ex) {
	    return new ResponseEntity<>("[\"Error Occured\"]",HttpStatus.INTERNAL_SERVER_ERROR);
	}
	if(res){
	    return new ResponseEntity<>("[\"List Reversed\"]",HttpStatus.OK);
	}else {
	    return new ResponseEntity<>("[\"List can't be reversed. Maybe link list is empty\"]",HttpStatus.OK);
	}
	
    }
    @RequestMapping(value = "/allNodes", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Integer>> getAllNodes(@RequestParam String name) {
	List<Integer> nodesFinal = new ArrayList<Integer>();
	try {
	    List<Node> nodes = nodeManager.listNodes(name);
	    for (Iterator iterator = nodes.iterator(); iterator.hasNext();){
                Node node = (Node) iterator.next();
		nodesFinal.add(node.getData());
            }
	} catch (Exception ex) {
	    return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	return new ResponseEntity<>(nodesFinal, HttpStatus.OK);
    }
}