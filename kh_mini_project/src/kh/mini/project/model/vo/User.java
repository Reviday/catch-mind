package kh.mini.project.model.vo;

import java.io.Serializable;
import java.util.Calendar;

public class User implements Serializable{
	/* 유저들의 정보를 관리하기 위한 클래스로,
	 * 서버에서만 접근한다.
	 */
	
	private static final long serialVersionUID = 6644685954737096019L;
	// 회원가입 입력정보
	private String id;
	private String pw;
	private String name;
	private String dateOfBirth; // 생년월일(990101)
	private String eMail;
	private int age; 			// dateOfBirth로 계산하여 저장
	private char gender;

// User 정보
	private int level; 		// 레벨
	private int exp; 		// Experience 경험치
	private int maxExp;  	// 레벨 구간별 최대 경험치량
	private int corAnswer;	// Cumulative number of correct answers (누적 정답 개수)
	
	private boolean loginState = false; // 로그인 상태
	
	
	public User() {	}
	
	//회원 가입용 매개변수 생성자
	public User(String id, String pw, String name, String dateOfBirth, String eMail, char gender) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.eMail = eMail;
		this.gender = gender;
		age = calcAge(dateOfBirth); // 한국나이계산
		//회원가입 상태이므로 나머지 설정값은 초기값으로
		level = 0;
		exp = 0;
		corAnswer = 0;
	}

	//유저 정보 관리용 매개변수 생성자
	public User(String id, String pw, String name, String dateOfBirth, String eMail, int age, char gender, int level,
			int exp, int corAnswer) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.eMail = eMail;
		this.age = age;
		this.gender = gender;
		this.level = level;
		this.exp = exp;
		this.corAnswer = corAnswer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getCorAnswer() {
		return corAnswer;
	}

	public void setCorAnswer(int corAnswer) {
		this.corAnswer = corAnswer;
	}
	
	public boolean isLoginState() {
		return loginState;
	}

	public void setLoginState(boolean loginState) {
		this.loginState = loginState;
	}

	
	// 출제자 또는 정답자에게 실행되는 메소드. 경험치와 누적정답 개수를 증가시킨다.
	public boolean expUpdate() {
		// 경험치를 10 누적 시키고, 누적 정답개수를 1 누적시킨다.
		exp += 10; corAnswer++;
		
		return levelUpCheck();
	}
	 
	/* 경험치 증가에 따른 레벨업 확인을 위한 메소드
	 * 경험치는 10으로만 주어지기 때문에, 
	 * 현재 레벨별 최대경험치량에 도달하여 레벨업을 할 시
	 * 잔여 경험치는 남지 않기 때문에 고려하지 않아도 된다.
	 */
	public boolean levelUpCheck() {
		boolean levelUp = false; // 레벨업 체크를 위한 불린값
		
		switch (level) {
		case 1:
			maxExp = 10;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 2:
			maxExp = 40;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 3:
			maxExp = 80;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 4:
			maxExp = 130;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 5:
			maxExp = 200;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 6:
			maxExp = 290;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 7:
			maxExp = 400;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 8:
			maxExp = 540;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 9:
			maxExp = 710;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 10:
			maxExp = 910;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 11:
			maxExp = 1150;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 12:
			maxExp = 1430;
			if (exp >= maxExp) { // 만약 경험치가 최대 경험치량에 도달하였을 때
				level++; // 레벨업
				exp = 0; // 경험치는 0으로 초기화
				levelUp = true;
			}
			break;
		case 13:
			//MaxLevel 이므로 레벨업 처리를 하지않는다.
			break;
		}
		
		return levelUp;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", pw=" + pw + ", name=" + name + ", dateOfBirth=" + dateOfBirth + ", eMail=" + eMail
				+ ", age=" + age + ", gender=" + gender + ", level=" + level + ", exp=" + exp + ", corAnswer="
				+ corAnswer + "]";
	}
	
	// 입력받은 생년월일을 현재 시간을 기준으로 한국나이를 계산하는 메소드
	public int calcAge(String dateOfBirth) {
		Calendar todayCal = Calendar.getInstance();
		Calendar userCal = Calendar.getInstance();
		int temp = Integer.parseInt(dateOfBirth);
		int birthDay = temp%100; temp /= 100;
		int birthMonth = temp%100; temp /=100;
		if(temp > 50) { // 50보다 높으면 2050년이 될 수 없으므로  1900년대로 
			temp += 1900;
		} else { // 50보다 작으면 1950년보다 적을 수 없으므로 2000년대로 
			temp += 2000;
		}
		userCal.set(temp, birthMonth, birthDay);
		
		long tempCal = (todayCal.getTimeInMillis() - userCal.getTimeInMillis())/1000/24/60/60;
		
		return (int)tempCal/365+1; // 한국나이계산
	}
	

}
