package kh.mini.project.paint;

public class Question {
   private String[] question; // 제시어 100개 저장용
   private String[] selected; // 라운드 진행하면서 중복을 방지하기위한 스택용 
   
   public Question(){
      question = new String[] {"사시나무", "오동나무", "카카오나무", "가격표", "기아타이거즈",
            "샴푸", "소방관", "돈다발", "카레이서", "삼국시대", "가로수", "걸음마", "열매", "우거지",
            "개인기", "귓속말", "퇴학", "도시", "백수", "초등학교", "대학생", "뉴스", "토익", "자바",
            "수학", "핸드폰", "배터리", "커피", "알까기", "피서", "일본어", "장식품", "위기", "강아지풀",
            "방송국", "부대찌개", "천연가스", "파인애플", "집중력", "아이스링크", "잠자리채", "공중전화",
            "인쇄소", "헛기침", "태양", "쓰레기차", "전달", "수갑", "연장전", "갈비뼈", "비둘기",
            "이모", "봄", "호떡", "가수", "화산", "비누", "수표", "비행기", "기차", "얼룩말", "지도",
            "얼음", "아이스크림", "한국", "북한", "미국", "중국", "유모차", "포세이돈", "화장실", "키보드",
            "마우스", "노트북", "이어달리기", "애호박", "잠수", "새우젓", "엿장수", "삼성", "애플",
            "카카오톡", "파티", "축구", "야구", "수영", "기린", "고속도로", "버스", "부산", "제주도", "스피커",
            "창문", "캠프파이어", "크리스마스", "공휴일", "소프라노", "모니터", "연필", "지우개"};
      selected = new String[12]; //라운드 수만큼 인덱스 크기를 가진다.
   }
   
   public String selQuestion(int round){ // 라운드를 인수로 받는다.(라운드가 곧 selected의 인덱스값)
      Pointer:
      while(true){
         // 0~99까지 인덱스 수를 랜덤으로 선정하여 제시어를 뽑는다.
         selected[round-1] = question[(int)(Math.random()*100)];
         
         
         for(int j=0;j<round-1;j++){
            // 중복이 발견되면 처음으로 돌아간다.
            if(selected[round-1].equals(selected[j])) 
               continue Pointer; 
         }
         
         // 중복이 발견되지 않으면 뽑힌 제시어를 리턴한다.
         return selected[round-1];
      }
   }
   
}