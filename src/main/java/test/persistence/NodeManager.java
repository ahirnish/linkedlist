package test.persistence;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator; 
 
import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.Query;

import test.model.Node;
import test.persistence.HibernateUtil;

import javax.persistence.*;

public class NodeManager{
    private static SessionFactory factory = HibernateUtil.getSessionFactory();

    /*Random String ID generation*/
    private String generateRandomString(){
	 return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    public void updateId(String nodeId, String newNodeId){
	Session session = factory.openSession();
        Transaction tx = null;
	try{
	    tx = session.beginTransaction();
	    Query query = session.createQuery("update Node set id = :newId" +
					      " where id = :oldId");
	    query.setParameter("newId", newNodeId);
	    query.setParameter("oldId", nodeId);
	    int result = query.executeUpdate();
	    tx.commit();
	}catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }	
    }
    
    /* Method to CREATE a node in the database */
    public String addNode(String headName, int data){
	if(headName==null || headName==""){
            return null;
	}
	Node headNode = getNode(headName);
	if(headNode!=null && !headNode.getHead()){
	    return null;
	}
	Node lastNode = headNode;
	while(lastNode!=null && lastNode.getNext()!=null){
	    lastNode = getNode(lastNode.getNext());
	}

	Session session = factory.openSession();
	Transaction tx = null;
	String nodeID = null;
	try{
	    tx = session.beginTransaction();
	    Node node = new Node();
	    node.setData(data);
	    node.setNext(null);
	    if(lastNode==null){
		node.setId(headName);
		node.setHead(true);
	    } else {
		node.setId(generateRandomString());
		node.setHead(false);
	    }
	    nodeID = (String) session.save(node); 
	    tx.commit();
	}catch (HibernateException e) {
	    if (tx!=null) tx.rollback();
	    e.printStackTrace(); 
	    nodeID=null;
	}finally {
	    session.close();
	    if(nodeID!=null && lastNode!=null){
		//update lastNode next = nodeID
		updateNode(lastNode.getId(),nodeID,true,false,false);
	    }
	}
	return nodeID;
    }

    public Node getNode( String name ){
	Session session = factory.openSession();
	Transaction tx = null;
	Node finalNode = null;
	if(name==null){
	    return null;
	}
	try{
            tx = session.beginTransaction();
	    finalNode = (Node)session.get(Node.class, name);
	}catch (HibernateException e) {
	    if (tx!=null) tx.rollback();
            e.printStackTrace();
	    finalNode=null;
	}finally {
	    session.close();
	}
	return finalNode;
    }

    public void updateNode(String prevNodeID, String nodeID, boolean updateNext, boolean updateHead, boolean headValue){
	Session session = factory.openSession();
	Transaction tx = null;
	try{
	    tx = session.beginTransaction();
	    Node prevNode = (Node)session.get(Node.class, prevNodeID);
	    if(updateNext){
		prevNode.setNext( nodeID );
	    } 
	    if(updateHead){
		prevNode.setHead(headValue);
	    }
	    session.update(prevNode); 
	    tx.commit();
	}catch (HibernateException e) {
	    if (tx!=null) tx.rollback();
	    e.printStackTrace(); 
	} finally {
	    session.close(); 
	}
    }

    public boolean deleteNode(String headName, int data){
	if(headName==null || headName==""){
            return false;
        }
        Node headNode = getNode(headName);
	if(headNode==null){
	    return false;
	}
	if(!headNode.getHead()){
	    return false;
	}
	String delId = null;
	Node newHead = null;
	if(headNode.getData()==data){
	    return false;
	} else {
	    Node prevNode = null;
	    Node currNode = headNode;
	    while(currNode!=null && currNode.getData()!=data){
		prevNode = currNode;
		currNode = getNode(currNode.getNext());
	    }
	    if(currNode==null){
		//data does not exist
		return false;
	    }
	    updateNode(prevNode.getId(),currNode.getNext(),true,false,false);
	    delId = currNode.getId();
	}

	Session session = factory.openSession();
	Transaction tx = null;
	Node delNode = null;
	try{
	    tx = session.beginTransaction();
	    delNode = (Node)session.get(Node.class, delId); 
	    session.delete(delNode); 
	    tx.commit();
	}catch (HibernateException e) {
	    if (tx!=null) tx.rollback();
	    e.printStackTrace(); 
	    delNode=null;
	}finally {
	    session.close(); 
	}
	if(delNode!=null){
	    return true;
	} else {
	    return false;
	}
    } 

    /* Method to  READ all nodes */
    @SuppressWarnings("unchecked")
    public List<Node> listNodes( String name ){
	Session session = factory.openSession();
	Transaction tx = null;
	List<Node> nodes = new ArrayList<Node>();
	Node headNode = getNode(name);
	if(headNode==null || headNode.getHead()==false){
	    return nodes;
	}
	Node currNode = headNode;
	while(currNode!=null){
	    nodes.add(currNode);
	    currNode = getNode(currNode.getNext());
	}
	return nodes;
    }

    public List<Node> getAllNodes(){
	Session session = factory.openSession();
	Transaction tx = null;
	List headNodes;
	try{
	    tx = session.beginTransaction();
	    Criteria criteria = session.createCriteria(Node.class);
	    criteria.add(Restrictions.eq("head", true));
	    headNodes = criteria.list();
	    tx.commit();
	}catch (HibernateException e) {
	    if (tx!=null) tx.rollback();
	    e.printStackTrace(); 
	    headNodes = null;
	}finally {
	    session.close(); 
	}
	if(headNodes!=null){
	    List<Node> nodes = new ArrayList<Node>();
	    for (Iterator iterator = headNodes.iterator(); iterator.hasNext();){
		Node headNode = (Node) iterator.next(); 
		nodes.addAll( listNodes(headNode.getId()) );
	    }
	    return nodes;
	} else {
	    return null;
	}
    }

    public boolean reverse(String headName){
	if(headName==null){
	    return false;
	}
	Node headNode = getNode(headName);
	Node newHead = null;
	if(headNode==null || headNode.getNext()==null){
	    return false;
	}
	if(!headNode.getHead()){
	    return false;
	}
	try {
	Node prevNode=null;
	Node nextNode = null;
	String newHeadId = generateRandomString();
	updateNode(headNode.getId(),null,false,true,false);
	updateId(headNode.getId(),newHeadId);
	Node currNode=getNode(newHeadId);
	while(currNode!=null){
	    nextNode = getNode(currNode.getNext());
	    if(nextNode!=null){
	    }
	    if(prevNode==null){
		updateNode(currNode.getId(),null,true,false,false);
	    } else {
		updateNode(currNode.getId(),prevNode.getId(),true,false,false);
	    }
	    prevNode=getNode(currNode.getId());
	    if(nextNode!=null){
		currNode=getNode(nextNode.getId());
	    } else {
		currNode=null;
	    }
	}
	newHead=getNode(prevNode.getId());
	updateNode(newHead.getId(),null,false,true,true);
	updateId(newHead.getId(),headName);
	}catch(HibernateException e){
	    return false;
	}
	return true;
    }
}