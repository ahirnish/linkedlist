package test.model;

import javax.persistence.*;

@Entity
@Table(name = "node")
public class Node {
        @Id
	@Column(name = "id")
	private String id;

    @Column(name = "data")
    private int data;

    @Column(name = "next")
    private String next;
    
    @Column(name = "head")
    private boolean head;

	public Node() {}
	public String getId() {
	    return id;
	}
	public void setId( String id ) {
	    this.id = id;
	}
	public int getData() {
	    return data;
	}
	public void setData( int val ) {
	    this.data = val;
	}
	public String getNext() {
	    return next;
	}
	public void setNext( String val ) {
	    this.next = val;
	}
    public boolean getHead() {
	return head;
    }
    public void setHead( boolean head ) {
	this.head = head;
    }
}
