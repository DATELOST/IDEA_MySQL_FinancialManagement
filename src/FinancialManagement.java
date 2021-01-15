package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class FinancialManagement {
    Connection con;
    FinancialManagement(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //连接数据库
            String url="jdbc:mysql://127.0.0.1:3306/mydata?useUnicode=true&" +
                    "characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8";
            con = DriverManager.getConnection(url, "root", "");
            //创建表
            firstCreateTable();
            //随机填充数据,只用运行一次
            //firstFillData();
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        catch (SQLException e) { e.printStackTrace(); }
    }
    //程序入口
    void start() throws SQLException {
        Scanner input=new Scanner(System.in);
        System.out.println("请选择操作\n0:查询客户信息\n1:查询理财产品\n" +
                "2:查询保险产品\n3:指定基金产品的上下线\n4:购买金融产品\nelse:退出系统");
        boolean flag = true;
        int op = -1,tmp;
        ArrayList<String> arrayList = new ArrayList<>();
        while(flag){
            switch (input.nextInt()){
                case 0:
                    System.out.print("请输入客户编号: ");
                    searchCustomerInfo(input.nextInt());
                    break;
                case 1:
                    System.out.print("请输入产品编号: ");
                    searchProductInfo(input.nextInt());
                    break;
                case 2:
                    System.out.print("请输入适用人群: ");
                    searchInsuranceInfo(input.next());
                    break;
                case 3:
                    System.out.print("上线/下线 0/1: ");
                    op=input.nextInt();
                    arrayList.clear();
                    System.out.print("请输入基金编号: ");
                    arrayList.add(input.next());
                    if(1==op) UpdateFund(true,arrayList);
                    else{
                        System.out.print("请输入基金名称: ");arrayList.add(input.next());
                        System.out.print("请输入基金类型: ");arrayList.add(input.next());
                        System.out.print("请输入基金金额: ");arrayList.add(input.next());
                        System.out.print("请输入基金管理: ");arrayList.add(input.next());
                        System.out.print("请输入风险等级: ");arrayList.add(input.next());
                        UpdateFund(false,arrayList);
                    }
                    break;
                case 4:
                    System.out.print("理财产品/保险/基金 0/1/2: ");
                    op = input.nextInt();
                    System.out.print("请输入客户编号: ");
                    tmp=input.nextInt();
                    System.out.print("请输入金融产品编号: ");
                    Buy(op,input.nextInt(),op);
                    break;
                default:flag=false;break;
            }
        }
    }
    //客户信息的查询
    void searchCustomerInfo(int cid) throws SQLException {
        String sql = "select * from Customer where cid=" + cid;
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        rs.next();
        System.out.println("客户编号: "+rs.getString(1));
        System.out.println("客户名称: "+rs.getString(2));
        System.out.println("邮箱: "+rs.getString(3));
        System.out.println("身份证: "+rs.getString(4));
        System.out.println("手机: "+rs.getString(5));
        System.out.println("密码: "+rs.getString(6));
        sql = "select * from BankCard where cid=" + cid;
        pst = con.prepareStatement(sql);
        rs = pst.executeQuery();
        while (rs.next()){
            System.out.println("银行卡号: "+rs.getString(1));
            System.out.println("类型: "+rs.getString(2));
        }
        sql = "select * from BuyProduct where cid=" + cid;
        pst = con.prepareStatement(sql);
        rs = pst.executeQuery();
        String res="";
        while (rs.next()) res+=rs.getString(2)+" ";
        System.out.println("购买理财产品: "+res);
        sql = "select * from BuyInsurance where cid=" + cid;
        pst = con.prepareStatement(sql);
        rs = pst.executeQuery();res="";
        while (rs.next()) res+=rs.getString(2)+" ";
        System.out.println("购买保险产品: "+res);
        sql = "select * from BuyFund where cid=" + cid;
        pst = con.prepareStatement(sql);
        rs = pst.executeQuery();res="";
        while (rs.next()) res+=rs.getString(2)+" ";
        System.out.println("购买基金产品: "+res);
    }
    //理财产品的查询
    void searchProductInfo(int pid) throws SQLException {
        String sql = "select * from Product where pid=" + pid;
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        rs.next();
        System.out.println("产品编号: "+rs.getString(1));
        System.out.println("产品名称: "+rs.getString(2));
        System.out.println("产品描述: "+rs.getString(3));
        System.out.println("购买金额: "+rs.getString(4));
        System.out.println("理财年限: "+rs.getString(5));
    }
    //符合人群类型的保险产品查询
    void searchInsuranceInfo(String who) throws SQLException {
        String sql = "select * from Insurance where iWho=" + String.format("'%s'",who);
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        while(rs.next()){
            System.out.println("保险编号: "+rs.getString(1));
            System.out.println("保险名称: "+rs.getString(2));
            System.out.println("保险金额: "+rs.getString(3));
            System.out.println("适用人群: "+rs.getString(4));
            System.out.println("保险年限: "+rs.getString(5));
            System.out.println("保障项目: "+rs.getString(6));
        }
    }
    //指定基金产品的上线下线 op=false/true
    void UpdateFund(Boolean op, ArrayList<String> data) throws SQLException {
        String sqlInsert = "insert into Fund values(?,?,?,?,?,?)";
        String sqlDelete = "delete from Fund where fid=?";
        PreparedStatement pst;
        if(op) {
            pst = con.prepareStatement(sqlDelete);
            pst.setString(1,data.get(0));
        }
        else {
            pst = con.prepareStatement(sqlInsert);
            for(int i=0;i<6;++i)pst.setString(i+1,data.get(i));
        }
        pst.executeUpdate();
    }
    //购买金融产品 op=0/1/2 -> 理财产品/保险/基金
    void Buy(int cid,int id, int op) throws SQLException {
        String sqlInsert="";
        switch(op){
            case 0: sqlInsert="insert into BuyProduct values(?,?)";     break;
            case 1: sqlInsert="insert into BuyInsurance values(?,?)";   break;
            case 2: sqlInsert="insert into BuyFund values(?,?)";        break;
        }
        PreparedStatement pst = con.prepareStatement(sqlInsert);
        pst.setInt(1,cid);
        pst.setInt(2,id);
        pst.executeUpdate();
    }
    //创建表
    private void firstCreateTable() throws SQLException {
        Statement st = con.createStatement();
        //创建客户表
        String sqlCreatTable = "CREATE TABLE IF NOT EXISTS Customer("
                + "cid int(12) not null auto_increment,"
                + "name varchar(20) not null,"
                + "mail varchar(20) not null,"
                + "idCard char(18) not null,"
                + "phone char(12) not null,"
                + "cipher varchar(20) not null,"
                + "primary key(cid)"
                + ")charset=utf8;";
        st.executeLargeUpdate(sqlCreatTable);
        //创建银行卡表
        sqlCreatTable = "CREATE TABLE IF NOT EXISTS BankCard("
                + "bid int(20) not null auto_increment,"
                + "bType char(12) not null,"
                + "cid int(12) not null,"
                + "primary key(bid),"
                + "constraint fkb foreign key(cid) references Customer(cid)"
                + ")charset=utf8;";
        st.executeLargeUpdate(sqlCreatTable);
        //创建理财产品表
        sqlCreatTable = "CREATE TABLE IF NOT EXISTS Product("
                + "pid int(12) not null auto_increment,"
                + "pName char(20) not null,"
                + "pDescription varchar(200) not null,"
                + "pMoney int(12) not null,"
                + "pDate char(12) not null,"
                + "primary key(pid)"
                + ")charset=utf8;";
        st.executeLargeUpdate(sqlCreatTable);
        //创建保险表
        sqlCreatTable = "CREATE TABLE IF NOT EXISTS Insurance("
                + "iid int(12) not null auto_increment,"
                + "iName char(20) not null,"
                + "iMoney int(12) not null,"
                + "iWho varchar(40) not null,"
                + "iDate char(12) not null,"
                + "iWhat char(20) not null,"
                + "primary key(iid)"
                + ")charset=utf8;";
        st.executeLargeUpdate(sqlCreatTable);
        //创建基金表
        sqlCreatTable = "CREATE TABLE IF NOT EXISTS Fund("
                + "fid int(12) not null auto_increment,"
                + "fName char(20) not null,"
                + "fType char(12) not null,"
                + "fMoney int(12) not null,"
                + "fManager char(12) not null,"
                + "fRisk char(8) not null,"
                + "primary key(fid)"
                + ")charset=utf8;";
        st.executeLargeUpdate(sqlCreatTable);
        //创建购买产品表
        sqlCreatTable = "CREATE TABLE IF NOT EXISTS BuyProduct("
                + "cid int(12) not null auto_increment,"
                + "pid int(12) not null,"
                + "primary key(cid,pid),"
                + "constraint fkbPc foreign key(cid) references Customer(cid),"
                + "constraint fkbPp foreign key(pid) references Product(pid)"
                + ")charset=utf8;";
        st.executeLargeUpdate(sqlCreatTable);
        //创建购买保险表
        sqlCreatTable = "CREATE TABLE IF NOT EXISTS BuyInsurance("
                + "cid int(12) not null,"
                + "iid int(12) not null,"
                + "primary key(cid,iid),"
                + "constraint fkbIc foreign key(cid) references Customer(cid),"
                + "constraint fkbIi foreign key(iid) references Insurance(iid)"
                + ")charset=utf8;";
        st.executeLargeUpdate(sqlCreatTable);
        //创建购买基金表
        sqlCreatTable = "CREATE TABLE IF NOT EXISTS BuyFund("
                + "cid int(12) not null,"
                + "fid int(12) not null,"
                + "primary key(cid,fid),"
                + "constraint fkbFc foreign key(cid) references Customer(cid),"
                + "constraint fkbFf foreign key(fid) references Fund(fid)"
                + ")charset=utf8;";
        st.executeLargeUpdate(sqlCreatTable);
    }
    void firstFillData() throws SQLException {
        Random random = new Random();
        String[] names = {"赵","钱","孙","李","周","吴","郑","何"};
        String[] type = {"货币","债券","股票"};
        String[] cards = {"工行","建行","农行"};
        String sqlInsert = "insert into Customer values(?,?,?,?,?,?)";
        String sqlInsert2 = "insert into BankCard values(?,?,?)";
        String sqlInsert3 = "insert into Product values(?,?,?,?,?)";
        String sqlInsert4 = "insert into Insurance values(?,?,?,?,?,?)";
        String sqlInsert5 = "insert into Fund values(?,?,?,?,?,?)";
        PreparedStatement pst = con.prepareStatement(sqlInsert);
        //客户
        for(int i=0;i<6;++i){
            pst.setInt(1,i+1);
            pst.setString(2,names[random.nextInt(8)]+names[random.nextInt(8)]);
            pst.setString(3,random.nextInt(1000000)+"@qq.com");
            pst.setString(4,"44060519760713795"+i);
            pst.setString(5,"1369519793"+i);
            pst.setString(6,random.nextInt(1000)+"");
            pst.executeUpdate();
        }
        //银行卡
        pst = con.prepareStatement(sqlInsert2);
        for(int i=0;i<6;++i)
            for(int j=0;j<3;++j){
                pst.setInt(1,i*3+j+1);
                pst.setString(2,cards[j]);
                pst.setInt(3,i+1);
                pst.executeUpdate();
            }
        //理财产品
        pst = con.prepareStatement(sqlInsert3);
        for(int i=0;i<6;++i){
            pst.setInt(1,i+1);
            pst.setString(2,"投资"+i);
            pst.setString(3,"上证指数");
            pst.setString(4,"123"+random.nextInt(10000));
            pst.setString(5,random.nextInt(20)+"个月");
            pst.executeUpdate();
        }
        //保险
        {
            pst = con.prepareStatement(sqlInsert4);
            pst.setInt(1,1);
            pst.setString(2,"意外险");
            pst.setString(3,"10000");
            pst.setString(4,"学生");
            pst.setString(5,"12个月");
            pst.setString(6,"意外事故");
            pst.executeUpdate();
            pst.setInt(1,2);
            pst.setString(2,"养老险");
            pst.setString(3,"20000");
            pst.setString(4,"职工");
            pst.setString(5,"120个月");
            pst.setString(6,"养老");
            pst.executeUpdate();
        }
        pst = con.prepareStatement(sqlInsert5);
        for(int i=0;i<6;++i){
            int t=random.nextInt(3);
            String h="";
            switch (t){
                case 0:h="低";break;
                case 1:h="中";break;
                case 2:h="高";break;
            }
            pst.setInt(1,i+1);
            pst.setString(2,"基金"+i);
            pst.setString(3,type[t]);
            pst.setString(4,"100"+random.nextInt(10000));
            pst.setString(5,names[random.nextInt(8)]+names[random.nextInt(8)]);
            pst.setString(6,h);
            pst.executeUpdate();
        }
    }
}
