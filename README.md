# linkedlist

1. Install maven
2. Install mysql, run it on localhost:3306 and set username='root' and pwd='root123' (All these are defined in hibernate.cfg.xml)
3. Create a schema with name 'ahirnish' in database
4. Create a table in 'ahirnish' with name 'node' with this command - create table node (id VARCHAR(50) NOT NULL,data INT default NULL,next  VARCHAR(50) default NULL,head  BOOLEAN  default NULL,PRIMARY KEY (id));
5. In the root folder(which has pom.xml), run 'mvn package'. It will create jar file.
6. Run java -jar target/test-spring-boot-0.1.0.jar
7. Go to http://127.0.0.1:8080/node/ for the application.
