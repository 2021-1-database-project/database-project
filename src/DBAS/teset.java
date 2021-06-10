package DBAS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class teset {

	public static void main(String[] args) throws SQLException{
		// TODO Auto-generated method stub
		String url  = "jdbc:postgresql://localhost/";
        String user     = "postgres";
        String password = "1234";
        try
        {
            Scanner scan = new Scanner(System.in);
            Connection connect = null;
            Statement st = null;
            ResultSet rs = null;
            
            int i;
            
            String query = null;
            
            System.out.println("SQL Programming Test");

          
            connect = DriverManager.getConnection(url, user, password);
            if(connect == null) {
            	scan.close();
                throw new SQLException("Connection Failure");
            }
            st = connect.createStatement();
            // JDBC를 이용해 PostgreSQL 서버 및 데이터베이스 연결
            
            while(true) {
            	System.out.println(
            			"\r\n값을 입력하세요.\r\n"
            			+ "0 : 프로그램 종료\r\n"
            			+ "1 : 배당금 상위 종목\r\n"
            			+ "2 : 사용자 배당금 계산\r\n"
            			+ "3 : 사용자 보유 주식\r\n"
            			+ "4 : 배당수익률 평균이상 종목\r\n");
            	int input = scan.nextInt();
            	if(input == 0) { // 종료
            		System.out.println("프로그램을 종료합니다.");
            		break;
            	}
            	else if(input == 1) { // 배당금 상위 10종목
            		System.out.println("배당수익률 상위 종목을 출력합니다. 몇 개의 종목을 출력할까요?");
                    int num = 0;
                    num = scan.nextInt();
                    query = "select stname from dividend where provideRate is not null order by provideRate desc limit " + num;
                    rs = st.executeQuery(query);
                    System.out.println("\t" + "stname");
                    System.out.println("------------------------------------------------------");
                    i = 1;
                    while(rs.next()) {
                        System.out.println("" + i + "\t" + rs.getString("stname"));
                        i++;
                    }

            	}
            	else if(input == 2) { // 사용자 배당금 계산
            		System.out.println("사용자의 id를 입력하세요.");
            		String inputid = scan.next();
            		query = "select * from stholder where stid = '" + inputid + "';";
                    rs = st.executeQuery(query);
                    
                    i = 1;
                    if(!rs.next()) {
                    	System.out.println(inputid + " : 해당 아이디는 존재하지 않습니다.");
                    	continue;
                    }
                    query = "select stname \r\n"
                    		+ "from dividend\r\n"
                    		+ "where dividend.stname in (select stname from stholder where stid = '" + inputid + "');";
                    rs = st.executeQuery(query);
                    if(!rs.next()) {
                    	System.out.println(inputid + " : 해당 아이디가 가진 주식 종목의 배당금이 존재하지 않습니다.");
                    	continue;
                    }
                    query = "select stid, stholder.stname, num, provide, num*provide as total\r\n"
                    		+ "from stholder, dividend\r\n"
                    		+ "where stholder.stname = dividend.stname\r\n"
                    		+ "and stid = '" + inputid + "';";
                    rs = st.executeQuery(query);
                    System.out.println("\t" + "stID" + "\t" + "stName" + "\t" + "num" + "\t" + "provide"+ "\t" + "total");
                    System.out.println("------------------------------------------------------");
                    while(rs.next()) {
                        String stID = rs.getString("stid");
                        String stName = rs.getString("stname");
                        String Num = rs.getString("num");
                        String Provide = rs.getString("provide");
                        String Total = rs.getString("total");
                        
                        System.out.println(i + "\t" + stID + "\t" + stName + "\t" + Num + "\t" + Provide+ "\t" + Total);
                        i++;
                    }
            	}
            	else if(input == 3) {
            		i = 1;
            		System.out.println("사용자의 id를 입력하세요.");
            		String inputid = scan.next();
            		query = "select * \r\n"
            				+ "from stholder \r\n"
            				+ "where stid = '" + inputid + "';";
                    rs = st.executeQuery(query);
                    System.out.println("\t" + "stID" + "\t" + "stName" + "\t" + "num");
                    System.out.println("------------------------------------------------------");
                    while(rs.next()) {
                        String stID = rs.getString("stid");
                        String stName = rs.getString("stname");
                        String Num = rs.getString("num");
                        
                        System.out.println(i + "\t" + stID + "\t" + stName + "\t" + Num);
                        i++;
                    }
            	}
            	else if(input == 4) {
                    i = 1;
                    st.executeUpdate("drop view avePR;");
                    String avePR = "create view avePR as select stname, provideRate from dividend where provideRate::float > (select avg(provideRate::float) from dividend)"; // 배당수익률 평균보다 높은 값을 가지는 종목들
                    st.executeUpdate(avePR);
                    query = "select * from avePR order by provideRate desc";
                    rs = st.executeQuery(query);
                    while(rs.next()) {
                        System.out.println("" + i + "\t" + rs.getString("stname") + "\t" + rs.getString("provideRate"));
                        i++;
                    }
                 }
            }

            
            rs.close();
            st.close();
            connect.close();
            scan.close();
        } catch (SQLException ex) {
            throw ex;
        }
    
	}

}
