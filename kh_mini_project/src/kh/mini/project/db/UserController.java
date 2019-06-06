package kh.mini.project.db;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import kh.mini.project.main.view.Main;
import kh.mini.project.model.vo.User;


public class UserController {
		
		
	//userDB에 모든 유저 데이터를 저장하는 메소드
	public void dataSaveAll(Vector allUser_vc) {
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("userDB.dat"))) {
			User [] users = new User[allUser_vc.size()]; //allUser_vc의 크기만큼 User 객체 배열을 생성
			// user_vc에 있는 User 객체 정보를 users에 저장한다.
			for(int i=0; i<users.length; i++) {
				users[i] = (User)allUser_vc.get(i);
			}
			// users에 저장된 User 객체를 userDB.dat에 저장한다.
			for(User u : users) {
				oos.writeObject(u);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace(); return;
		} catch (IOException e) {
			e.printStackTrace(); return;
		}
	}
	
	//userDB의 데이터를 찾아오는 메소드
	public User dataSearch(String id) {
		Vector users = dataLoadAll();
		for(int i=0; i<users.size(); i++) 
		{
			User u = (User)users.elementAt(i);
			if(u.getId().equals(id)) {
				return u;
			}
		}
		return null;
	}
	
	//userDB의 모든 데이터를 불러오는 메소드
	public Vector dataLoadAll() {
		Vector users = new Vector();
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userDB.dat"))) {
			;
			User user = new User();
			while((user=(User)(ois.readObject()))!=null) {
				users.add(user);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace(); 
		} catch (EOFException e) {
			System.out.println("모든 회원 정보 로드 완료!");
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		return users;
	}
	// 어드민 추가 테스트
//	public static void main(String[] args) {
//		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("userDB.dat"))){
//			User admin = new User("admin", "admin", "admin", "900101", "null", 0, ' ', 999, 0	, 99999);
//			System.out.println("어드민 저장완료!");
//		} catch (Exception e) {
//		}
//	}
}
