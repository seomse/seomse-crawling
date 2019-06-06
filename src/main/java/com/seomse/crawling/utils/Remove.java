/** 
 * <pre>
 *  파 일 명 : Remove.java
 *  설    명 : 데이터 크롤링용 임시 클래스
 *  
 *                    
 *  작 성 자 : yeonie(김용수)
 *  작 성 일 : 2017.12
 *  버    전 : 1.0
 *  수정이력 : 
 *  기타사항 :
 * </pre>
 * @author  Copyrights 2017 by ㈜섬세한사람들. All right reserved.
 */

package  com.seomse.crawling.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Remove {
	
	/**
	 * 특수문자를 제거한다.
	 * @param str 문자열
	 * @return
	 */
	public static String special(String str){	   
	   String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
	   str =str.replaceAll(match, "");
	   return str;
	}
	
	/**
	 * .(dot) 을 제외한 특수문자를 제거한다.
	 * @param str 문자열
	 * @return
	 */
	public static String specialNotDot(String str){	   
	   String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s.]";
	   str =str.replaceAll(match, "");
	   return str;
	}
	

	/**
	 * .(dot)과 ,(comma)를 제외한 특수문자를 제거한다.
	 * @param str 문자열
	 * @return
	 */
	public static String specialNotDotComma(String str){	   
		String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s.,]";
		str =str.replaceAll(match, "");
		return str;
	 }
	
	/**
	 * HTML테그를 제거한다
	 * @param str 문자열
	 * @return
	 */
	public static String htmlTag(String str){	
		str = enterContinue(str);
		str = spaceContinueTab(str);
//		"<(.|\n)*?>";
//		String tag = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";  
//		String tag = "<(|\\n)*?>";  //<> 전부 다지움
		
		String tag = "<(/)?([\\?:a-zA-Z0-9\\-\\s-]*)(\\s[\\?:_a-zA-Z0-9\\s-]*=[^>]*)?(\\s)*(/)?>";
		String entry = "&[a-z0-9#]+;";
		String help = "<!--.*?-->|-->.*?";
		
		str =str.replaceAll(help, "");
//		str =str.replaceAll(tag, "");
   
	 	str =str.replace("<br>", "\n");
	 	
		str = str.replaceAll("<(/|[\\s]*/|/[\\s]*)?(br|BR)([\\s]*/|[\\s]*/[\\s]*)?>", "\n");
	 	str = str.replaceAll("<(p|P)[^>]*>", "\n\n");
	 	str = str.replaceAll("<\\/b>", "");
	 	str = str.replaceAll("<\\/B>", "");
	 	str = str.replaceAll(tag, "");
	 	//주석제거
	 	
	  	str =str.replace("&nbsp;", " ");
		str =str.replace("&#160;", " ");
		str =str.replace("&lt;", "<");
		str =str.replace("&#60;", "<");
		str =str.replace("&gt;", ">");
	  	str =str.replace("&#62;", ">");
	  	str =str.replace("&amp;", "&");
	  	str =str.replace("&#38;", "&");
	  	str =str.replace("&#39;", "'");
	 	str =str.replace("&#xD;", "'");
	 	
		str =str.replaceAll(entry, ""); //기타엔트리제거
		//&amp = &, &#39 = '.   //&#숫자 = 아스키 코드넘버 
//		http://www.w3schools.com/html/html_entities.asp   엔트리표
   	 	str =str.trim();
		return str;
	}
	
	/**
	 * 이중공백을 하나의 스페이스로 치환한다.
	 * @param str 문자열
	 * @return String
	 */
	public static String spaceContinue(String str){
		 String match2 = "\\s{2,}";
		 str = str.replaceAll(match2, " ");
		 return str;
	}
	
	
	 /**
	 * 2개이상의 Space,Tab가 하나의 Space로 치환된다.
	 * @param str 문자열
	 * @return String
	 */
	public static String spaceContinueTab(String str){
		return str.replaceAll("\\p{Blank}{2,}", " ");
	}
	
	/**
	 * 두개이상의 엔터가 하나의 엔터로 치환된다. 
	 * @param str 문자열 
	 * @return String
	 */
	public static String enterContinue(String str){
//		return str.replace("\\n{2,}", "\n");
//		return str.replaceAll("[\\n]+", "\n");
		
		return str.replaceAll("[\n|\r|\r\n]+", "\n");
	}
	
   /**
	* 엔터를 제거한다.
	* @param str 문자열
	* @return
	*/
	public static String removeUnlimiteEnter(String str){
		return str.replaceAll("[\n|\r]+", "");
	}

	/**
	 * Removing the number of other
	 * 숫자(0~9)외 모든 문자 제거
	 * @param str 문자열
	 * @return String
	 */
	public static String getNumber(String str){
		return str.replaceAll("[^0-9]*", "");
	}
	
	/**
	 * Removing the Hangul Syllables of other
	 * 한글완성형 이외 모든 문자 제거
	 * @param str
	 * @return String
	 * @Http http://www.unicode.org/charts/PDF/UAC00.pdf
	 */
	public static String notHangulSyllables(String str){
		return hangulJamo(str.replaceAll("[^가-힣]*", ""));
	}
	
	/**
	 * Removing the Hangul Syllables
	 * 한글완성형 문자 제거
	 * @param str
	 * @return String
	 * @exception 자모삭제안됨
	 * @Http http://www.unicode.org/charts/PDF/UAC00.pdf
	 */
	public static String hangulSyllables(String str){
		return str.replaceAll("[가-힣]*", "");
	}
	
	/**
	 * Unfinished
	 * 예 ㅁ ㄴ ㅡ ㅣ 형태의 자모 제거
	 * @param str 
	 * @return null
	 */
	public static String hangulJamo(String str){
		return str.replaceAll("[ㄱ-ㅎ|ㅏ-ㅣ]*", "");

	}
	
	
	/** 160430 임시추가 제거 예정 seronie */
	/**
	 * \n \t 제거
	 * 160430 임시추가 seronie
	 * @param str
	 * @return
	 */
	public static String removeSpace(String str) {
		str = str.replace("\n", "");
		str = str.replace("\t", "");
		return str;
	}
	public static String specialEmpty(String str){
		str = special(str);
		str = str.replace(" ", "");
		str = str.replace("\r", "");
		str = str.replace("\n", "");
		str = str.replace("\t", "");
		return str;
	}
	public static String unlimiteSpace(String str){
		return str.replaceAll("\\p{Blank}{2,}", " ");
	}
	public static String emptyToSpace(String str){
		 return str.replaceAll("\\s{2,}", " ");
	}
	public static String removeUnlimiteSpace(String str){
		return str.replaceAll("\\p{Blank}", "");
	}
	public static String convertHtmlSpecial(String str){
		str =str.replace("&nbsp;", " ");
		str =str.replace("&#160;", " ");
		str =str.replace("&lt;", "<");
		str =str.replace("&#60;", "<");
		str =str.replace("&gt;", ">");
	  	str =str.replace("&#62;", ">");
	  	str =str.replace("&amp;", "&");
	  	str =str.replace("&#38;", "&");
	  	str =str.replace("&#39;", "'");
	 	str =str.replace("&#xD;", "'");
	 	return str;
	}
	/**
	 * 원문문자열에서 시작과 끝의 위치만큼을 제거해서 돌려준다.
	 * @param str
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public static String startEnd(String str, int startIndex, int endIndex){
		str.substring(startIndex+1, endIndex);
		
		
		//위치조건을 제외한 구문생성하기
		StringBuilder resultBuilder = new StringBuilder();
		resultBuilder.append(str.substring(0, startIndex));
		
		if(endIndex != str.length()-1){
			resultBuilder.append(str.substring(endIndex+1));
		}
		
		
		return resultBuilder.toString();
	}
	
	/** 160430 임시추가 seronie */
	
	/**
	 * 리스트 중복 제거
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<T> duplicationList(List<T> list){
		Set<T> set = new LinkedHashSet<T>(list);
		list = new ArrayList<T>(set);
		return list;
	}
	
	public static void main(String [] args){
		
		String test ="<iframe _src=\"/main/readVod.nhn?oid=448&aid=0000191974&foreignPlayVod=true\" width=\"544\" height=\"342\" frameborder=\"0\" marginheight=\"0\" marginwidth=\"0\" title=\"영상 플레이어\" allowfullscreen>[앵커]";
		
		System.out.println(htmlTag(test));
		
//		String enterTest = "\n<script src=\"json2.js\"></script>동물학대자와 범죄자들 <td cl-ass=\"pt_text\" name=\"hl\"></td><div cl-ass=\"bbs_content\"/> 어제 미디어 다음의 블로거 뉴스에서 이천 시민들이 분노를 표현하는 퍼포먼스의 하나로 아기돼지를 줄에 묶어서 찢어 죽였다는 기사와 그 기사에 달린 \"채식주의자가 아니면 입을 다물라\"라는 댓글들을 보고 충격을 금할 수 없었다.  사람들은 \"분노를 표현하기 위해서 공공의 장소에서 동물을 잔인하게 죽이는 행위\"를 아무렇지도 않게 여기는 듯 했다.  동물을 잔인하게 죽이고 학대하는 것이 왜 문제가 되느냐고? 그리고 어린 아이들이나 사람들이 그런 장면을 보는 것이 왜 문제가 되느냐고? 그건 동물학대와 범죄와의 상관 관계에 대해서 알지 못하는 사람들의 질문일 것이다.  작년 12월, 미국에서는 안토니 파넬리라는 35살의 남자가 세 명의 아이들 앞에서 자신의 부인을 물어 뜯겠다고 협박하며, 가족의 반려동물인 토끼를 목졸라 살해한 죄로 체포되었다. 그가 보석을 신청하기 힘들도록, 파넬리의 보석금은 무려 5만달러로 책정이 되었으며, 이번주 금요일 3급 폭행 혐의로 고등법원에 서게 될 것이다.  사실 길거리에서 아기돼지를 찢어 죽여도, 혹은 우울증을 앓는 아내의 눈 앞에서, 아내가 아끼는 소중한 고양이를 아파트에서 던져서 죽여버려도 아무런 벌을 받지 않는 한국에서라면, 고작 토끼 한 마리가 죽은 이 사건은 고등법원까지 가기는 커녕, 체포가 되기도 힘든 상황이었을 것이다. 그러나 현재 그는 미국에서 \"사건을 일으킬 가능성이 높은 가장 위험한 인물\"로 분류가 되어, 가족에게 접근할 수조차 없도록 구금되어 있는 상태이다.  대체 동물을 학대하는 것과 범죄 사이에 어떤 상관 관계가 있길래, 파넬리는 토끼를 죽인 그날부터 지금까지 형무소에 갇혀 있으면서, 재판을 기다리고 있는 것일까?  이 질문에 대답을 하기 위해서는 동물 학대와 범죄와의 상관 관계를 한 번 짚어 볼 필요가 있다.  1. 대부분의 연쇄살인범들은 동물학대 경력이 있다.  FBI의 조사 결과 대부분의 연쇄살인범들은 동물학대 경력이 있다고 한다. FBI 요원 레슬러의 말을 인용하자면, 이 범죄자들이 어린 아이였을 때 누구도 <강아지의 눈을 찌르는 것은 나쁜 짓>이라고 가르쳐 주지 않았다고 한다. ▶어린 시절 개나 고양이를 묶어 놓고 화살을 쏘곤 했던 알버트 데살보는 자라서 13명 이상(추정)의 여성을 목졸라서 살해했다.   <제프리 대머에게 토막 살해당한 희생자들>▶어린 시절 개구리나 고양이, 개의 머리를 잘라서 꼬챙이에 꿰고 놀았던 제프리 대머는, 자라서 17명(추정) 이상의 남자와 소년을 토막 살해했다. <사진출처: <a cl-ass=\"con_link\" href=\"http://www.assustador.com.br/killers/11.jpg\" target=\"_blank\">http://www.assustador.com.br/killers/11.jpg>   ▶BTK 킬러로 불리우는 칸사스의 데니스 레이더는 어린 시절 개와 고양이의 목을 매달아 죽였고, 자라서는 애니멀 컨트롤 센터에서 유기견을 포획하고, 동물로 인한 분쟁을 해결하는 직업을 가졌다. 이 시절 그는 이웃의 개를 아무런 이유없이 안락사 시키기도 했다고 한다. 그는 1991년까지 약 10명의 사람을 살해했다. <사진 출처: 위키피디아>  <레이더의 희생자들> 출처: <a cl-ass=\"con_link\" href=\"http://www.kansas.com/btk/\" target=\"_blank\">http://www.kansas.com/btk/> ▶ 정신과 교수인 리 보이드 말보박사가 10명의 사람들을 총으로 쏘아서 살해한 10대를 진찰한 결과, 이 살인자는 14살 무렵 수많은 고양이들을 새총으로 돌을 쏘아서 죽인 경험을 고백했다.   2. 학교 총격 사건 범인 중 50%가 동물학대 경력 동물학대와 범죄의 연관성은 단순히 연쇄 살인마들에게서 그치지 않는다. 최근 조승희 사건으로 한국에서도 많은 사람들이 관심을 가지게 된 일명 \"캠퍼스 슈팅 (학교 총격 사건)\"의 경우 범인들 중 50%가 지속적으로 동물을 학대한 경력이 있다고 한다.	▶ 15살의 나이에 자신의 부모를 포함, 4명을 살해하고 25명의 학우들에게 중상을 입힌 킵 킨켈은 소를 총으로 쏘고, 수 많은 고양이들을 목을 벤 경력이 있다. <사진 출처: 위키피디아>   ▶ 1997년 자신의 모친을 살해한 뒤, 라이플을 쥐고 학교에 들어가 2명을 죽이고 다수에게 상처를 입힌 루크 우드햄은 자기 자신의 반려견인 스파클이 죽을 때까지 고문을 한 경력이 있다. 루크 우드햄은 개가 죽어가면서 지르는 단말마를 \"아름다운 것\"이라고 표현했다고 한다. <사진 출처: <a cl-ass=\"con_link\" href=\"http://www.crimelibrary.com/\" target=\"_blank\">http://www.crimelibrary.com>  ▶ 11살의 앤드류 골든은 평소 친구들에게 자신이 개를 쏘아 죽인다고 자랑삼아 떠들고 다녔다고 한다. 그의 집에서 키우던 반려견이 의문의 죽음을 당한 며칠 후 앤드류 골든은 총을 들고 학교를 찾아가 4명의 학급친구와 1명의 선생님을 살해했다.  3. 동물학대와 가정 폭력, 아동 학대 심리학자들의 연구에 따르면 동물 학대를 하는 사람들은 미래에 가정에서 폭력을 행사할 가능성이 매우 높다고 한다. 실제로 가정폭력에 희생된 부인들의 71%가 남편이 반려동물을 죽이거나 죽이겠다고 협박한 적이 있다고 보고되었다.  남편으로부터 상습 구타를 당하는 아내들 5명 중 4명은 남편이 폭력을 휘두를 때마다 집에서 기르는 애완동물도 함께 얻어맞는다고 보고 되었으며, 가정문제 상담가들은 \"사람에 대한 학대와 동물 학대는 깊은 관련성이 있다\"며 \"배우자를 폭행한 후에는 집에서 기르는 애완동물이 다음 표적이 되며, 마지막으로 자녀들을 폭행하는 단계를 거치게 된다\"고 주장한다. 또한 아동학대로 고발당한 57개의 가정을 조사한 결과, 88%의 가정이 동물을 학대한 경력이 있으며, 이 중 2/3는 아이들이 말을 듣게 만들기 위해서 부모가 동물을 죽이거나 상해를 입힌 경우이고, 나머지 1/3은 아이들이 화풀이를 하기 위해서 동물을 학대한 것이다. 미국의 심리학자 프랭크 아시온 교수는 동물에 대한 학대를 엄중히 다루는 것이 바로 가정폭력 예방의 길이라고 주장한다.   4. 호주, 강간 살인범 100% 동물학대 경력 뉴사우스웨일즈 신문에 따르면 호주 경찰 조사 결과 강간 살인범 100%가 동물을 학대한 경험이 있다고 한다.  모나쉬 대학의 심리학자 엘레노라 글론은 동물을 잔인하게 학대하는 행위가 잔인한 범죄의 전 단계라는 것을 이해하면 범죄 예방에도 기여할 수 있다라고 이야기 한다.  80년대부터 지속된 이런 연구 결과에 따라 미국에서 \"동물학대범\"은 \"잠재적인 범죄자\"로 취급을 받는 강력한 동물학대법이 제정 되었고, 아이들 앞에서 토끼 한 마리를 죽인 파넬리 역시 <동물학대범 = 잠재적인 범죄자>로서 수감되어 고등법원에서 재판을 받게 된 것이다. 한국의 범죄자라고 해서 다르지는 않다. 아직까지 동물학대와 범죄자 간의 체계적인 연구 결과가 한국에서는 발표된 바 없는 것으로 알고 있지만, 연쇄살인범인 유영철은 어린시절 동물을 거리낌 없이 죽였다고 한다.  ▶ 왜 동물을 학대할까?  우리는 누구나 분노의 감정을 겪게 된다. 이런 경우 보통의 사람들은 폭력이 아닌 방법을 사용하여 분노를 표현하거나, 자신의 감정을 조절하기 위해 노력한다.  그러나 일부 사이코패스들의 경우 자신보다 물리적으로 약한 존재를 희생양으로 삼아서 억압된 분노와 감정을 표출한다. \"바늘 도둑이 소도둑\" 되는 것처럼, 희생양은 작은 곤충에서부터 시작해서 동물을 거쳐 마침내 사람을 희생양으로 삼게 되는 경우가 대부분이라고 한다. ▶ 대한민국에서도 동물학대는 범죄행위가 된다.  2008년부터 시행되는 새로운 동물 보호법 제7조에 의하면, 누구든지 목을 매다는 등의 잔인한 방법으로 동물을 죽이거나, 노상 등 공개된 장소에서 죽이거나 같은 종류의 다른 동물이 보는 앞에서 죽이는 행위 등을 하면 500만원 이하의 벌금이 부과된다. 나는 가끔 길거리에서 길냥이들에게 돌을 던지는 아이들을 본다. 부모가 옆에 있으면서도 아무렇지도 않게 바라보는 경우도 있다. 내가 약한 동물에게 돌을 던지지 말라고 아이들을 말리면, 오히려 부모들이 나에게 화를 내기도 한다.  언젠가는 자그마한 병아리를 조물거리면서 들고 다니는 아이들을 본 적이 있다. \"병아리가 아픈 것 같으니, 어두운 곳에 넣어 놓고 따뜻하게 해줘야 살 것\"이라고 오지랖 넓게 참견을 하자 아이들은 \"원래 다 죽어가던 거에요.\"라고 아무렇지도 않게 대답했다.  누가 이 아이들을 이렇게 만든 것일까. 생명의 귀중함을 모르는 아이들이 자라서 어른이 되면 어떻게 될까?  그러나 아이들을 가르쳐야 할 의무가 있는 어른들이 아무런 거리낌 없이 잔인한 장면을 보여주면서, 아이들에게 무엇을 가르칠 수 있을까? 화가 나면 동물을 찢어 죽이라고 가르칠 것인가? 기분이 나쁘면 자신보다 약한 생명체에게 화풀이를 하라고 가르칠 것인가? 이천 시민들의 아기 돼지 찢어 죽이기와 이를 옹호하는 댓글들을 보며 마음이 아파온 건, 자신의 분노를 표출하기 위해서라면 자기보다 약한 생명을 잔인하게 찢어 죽이면서, 이를 어린 아이들을 포함한 공공에게 내보이는 것이 정당하다고 생각하는 사람들이 만들어 갈 미래가 두려웠기 때문이다.  우리는... 대한민국은 어디를 향해 가고 있는가...	 <hr cl-ass=\"hide\">   폭력범죄와 동물학대의 상관관계 \n<오고뇨크>에 발표된, 구소련의 한 연구를 보면 폭력범죄자 집단의 87% 남짓이 어린 시절에 가축을 태우거나, 목매달거나 찔러 죽인  적이 있음을 알 수 있다고 합니다...  미국에서도, 예일대학 스티븐 켈러트 박사가 동물을 학대하는 어린이가 폭력범죄자가 될 가능성이 훨씬 크다는 사실을 밝혀낸 적이 있 습니다....  또 미국의 교도소 수감자에 관한 많은 연구들 역시, 범죄자들은 어릴 때 애완동물을 가져본 적이 거의 없다는 사실을 밝혀냈다고 합니다...  그들 중 어느 한사람도, 다른 존재의 생명을 존중하고 보살피는 데서, 보람을 느껴볼 기회를 가져보지 못했던 것입니다.  그러나 이런 태도는 얼마든지 뒤바뀔 수 있다고 합니다. 범죄자들도 마찬가지...  한번은 출감일 얼마 남지 않은 범죄자들에게, 감방안에서 애완용 고양이를 데리고 있도록, 허영한 따스한 연구가 행해진 적이 있었지요.  결과는 어땠을까요? \"고양이를 사랑하며 보살핀 사람들 중에는, 뒤에 자유인으로서 사회에 적응하는 데 실패한 사람이 단 한 사람도 없었다.\"  석방된 범죄자의 70%이상이 다시 범죄를 저지르고, 감옥으로 돌아오는 것이 보통인 현행 사법제도하에서 이런 결과가 나왔다는 건 놀라운  일입니다....  (Kellert, Stephen R.and Felthous.\"Childhood Cruelty Toward animals Among Criminals and Noncriminals\")  <div cl-ass=\"autosourcing-stub\">	  <hr cl-ass=\"hide\"> <span cl-ass=\"f20 lh23 bld wsp_1\">\"그런 사람일줄이야..\" 살인마의 이중생활  <td cl-ass=\"subtit_news1 bld\" style=\"padding-top: 5px;\">시대의 연쇄살인범 강호순. 이웃 주민들은 경악 <div cl-ass=\"space20\">   <span cl-ass=\"font_news\" id=\"content\" style=\"font-size: 15px;\">오늘(30일) 오전 경찰은 강호순이 부녀자를 암매장한 곳에서 시신 발굴 작업을 벌여 4구를 수습했습니다.\n2년 넘게 암매장 돼 있던 첫번째 희생자 배모씨의 시신은 백골 상태로 한점한점 수습됐습니다.\n굴삭기까지 동원된 작업으로 오늘 하루 발굴된 시신은 4구.\n경찰이 이에 앞서 지난 25일 군포 여대생 시신과  2007년 5월에 37살 박모씨의 시신을 수습했기 때문에 골프연습장이 들어서 발굴하지 못한 1구를 제외한 6구의 시신이 모두 발굴된 것입니다.\n한편 강호순의 이웃 주민들은 \"예의바르고 성실한 사람인줄만 알았다\"며 피의자의 이중성에 경악을 금치 못했습니다. .\n함께 보시죠.\n(SBS 인터넷뉴스부)\n				 출처 >http://news.sbs.co.kr/section_news/news_read.jsp?news_id=N1000537029   동물학대와 강력범죄는 깊은 상관관계가 있다.아무나 자신이 키우던 개를 직접 잡아 죽이지 못한다.  개를 죽이는 살생을 계속 하다보니 내 눈동자가 이상해지더란 것은 죄의식을 느끼기엔 이미 상황이 늦었다는 것이 아닐까. 그러한 살생에 대한 정신적인 폐해가 심해서, 생명경시 풍조가 그의 뇌리에 못 박아 버린것 같다는 생각이 든다. 지금 한국에서 동물학대의 희생양이 되는 동물들은 사람들이 애완동물 대표격으로  많이 키우는 개와 고양이다.  한국에서 개와 고양이 먹는 풍습을 정부에서 강력하게 금지 시키지 않는다면, 한국 사회에서 만연한 동물학대는 앞으로 없어지지 않을 것이며, 강력범죄가 줄여 들거란 기대도 하기 힘들다.  한국의  일부 보신족들은 가족같은 친구같은 존재의 반려동물들을 먹는 것에 대해서  아무런 죄의식을 느끼지 않는 사람들이 많다. 반려동물 대표격인 개와 고양이를 키우는 사람들은 보신족들에 의해서 개.고양이가 식용으로 쓰이는 문제에 대해서 많이 고통스러워 한다.  말 못하는 속 앓이를 한다는 것이다. 즉 가슴이 아프고 슬프다는 것이다.  보신탕, 영양탕, 통개라는 간판이나 글자만 봐도 살이 떨리고  괴롭다. 내 개와 고양이가  가족같고 친구 같으니, 보신으로 희생되는 불쌍한 개와 고양이를 지켜보는 것이 큰 고통이다 라는 것이다.  하지만 개를 먹는 사람들은  개식용때문에 고통스러워 하는 다수의 한국인 이웃에 대한 배려심이 없다.  개와 고양이를 먹지 않는  다수의 지구촌 사람들에 대한 배려심이 없다. 이미 개와 고양이는 전세계적인 대표 애완격으로  많이 키움에도 불구하고 사람들이 친숙해 하는 반려동물을 잡아 먹는 것에 대해서 아무런 저항감과 꺼리낌, 죄의식을 가지지 않으며, 그에 대한 행동을 합리화 시키기 위해서 원숭이골, 푸아그라를 끌어들인다. 아님 축산동물들을 끌어 들이거나 (실제로  축산동물들 고통은 안중에도 없으면서 말이다.)이건 큰 문제가 아닐 수 없다.  개를 먹는 것 때문에 다른사람들이 고통스럽다는데, 그러한  고통을 외면하고, 사람들과 친숙한 동물들이 당하는 고통을 외면하는  죄책감 느끼지 못하는 행동은 정서적인  감성이 많이 황폐해져 있다는 것이다. 도덕성이 많이 결함되었다는 것이다.이런 정서적인 감성이 황폐되어 있다면, 욱 하는 성격에 못 이겨  상대방에게 폭력으로 행하여지며  약자에 대한 연민과 애정이 없다.  강자에겐 약하고, 약자에겐 강한 비굴한 면도 서슴없이 이루어진다.  모든나라에서 그렇지만 한국에서도 여성은 사회적인 약자에 속한다. 동물도 약한존재이다. 한국에서 일어나는 강력범죄 대부분은 부녀자 살인 사건과 강간사건이 주를 이루며, 동물학대의 주 희생양은 개와 고양이다.일부 보신족들은 여성을 성의 노리개로 생각하는 몰 지각한 사람들도 있으며, 자의든 타의든 동물들이 맹목적인 보신으로 처참하게 희생되어도 남몰라라 한다. 서양에서는  동물학대자와 살인를 저지른 살인범들을 똑같이 취급하고 분류되어 진다.  동물학대자는 언제든지 사람을 해 할수 있다고 판단하여 요주의 인물로 관찰하고 지켜본다.  한국에서도 더이상 동물학대문제, 개식용 문제에 대해서 간과하지 말았으면 좋겠다. 한국사람들의 정서적인 함양을 위해서라도 개.고양이 먹는 풍습을  제재 하는 것이 마땅하다. 사람과 친숙한 개.고양이 죽이는 것을  아무렇지도 않게 행하는 사람은 그에 대한 죄의식을 못 느껴,  언제든 사람을 해 할 수 있는 소지가 다분하므로 말이다.  동물과 사람은 별개가 아니다. 동물들이 학대 당하고 끔찍하게 죽임을 당하는 사회에서 사람이라고 해서 그런일 당하지 않는다는 보장은 없다.		<span cl-ass=\"f20 lh23 bld wsp_1\">강호순은 전형적인 '사이코 패스'  <td cl-ass=\"subtit_news1 bld\" style=\"padding-top: 5px;\"><div cl-ass=\"space20\">  \n<span cl-ass=\"font_news\" id=\"content\" style=\"font-size: 15px;\">경기 서남부 지역에서 7명의 부녀자를 연쇄살해한 것으로 드러난 강호순(38)은 여성들에게 살인충동을 느끼고 사냥하듯 접근해 잔혹하게 살해한 범행 수법에서 전형적인 사이코패스(Psycho-path: 반사회적 인격장애)의 모습을 보여줬다는 지적이다.\n30일 경찰에 따르면 강호순은 피해 여성들에게 성폭행이나 성관계를 위해 접근했다가 더 이상 필요하지 않게되면 아무런 거리낌 없이 곧바로 살해했다.\n특히 희생자 대부분을 스타킹으로 목졸라 살해한 뒤 시신을 알몸 상태로 매장하는 등의 유사한 범행 수법을 계속 되풀이했다.\n경찰대학교 표창원 교수는 \"사이코패스의 일반적 특징은 타인의 감정이나 정서 등을 전혀 공감하지 않고 자기 잘못을 반성할 줄 모르며, 거짓말을 능수능란하게 하면서도 양심의 가책을 받지 못하는 것\"이라며 \"강호순은 이런 사이코패스의 요소들을 거의 다 보이고 있다\"고 진단했다.\n피해여성 7명 중 3명은 노래방 도우미, 4명은 버스정류장에서 버스를 기다리는 여성들이었다는 점에서 강호순은 덫을 놓고 기다리는 '사냥꾼'과 다르지 않았다.\n검거 이후에도 양심의 가책은커녕 수사관들에게 '증거가 있으면 제시해보라'는 식의 당당한 태도를 보였다.\n경기대 범죄심리학과 이수정 교수는 \"강호순은 성적 욕망을 위해 사냥하듯 접근해 희생자들을 비인격적 '도구'로 생각했다. 또 본인이 잘못해 놓고 경찰에는 증거를 갖고 오라고 되레 큰소리쳤다\"며 \"이는 현재 상황에 대한 영웅의식이 강하고 죄의식은 없는 전형적인 사이코패스\"라고 규정했다.\n범인 강은 이웃에는 '아이들에게 잘하는 친절하고 자상한 아버지'의 이미지를 줬던 반면 함께 살았던 전 부인 등에게는 폭력적인 성향을 보였는데 이런 '다중인격' 역시 사이코패스의 모습이라는 분석이다.\n고향 주민들은 강호순이 집안 형편이 나쁘지 않았지만 어릴 때부터 남의 물건을 자주 훔치고 거친 행동을 잘했던 것으로 증언하고 있다.\n서울아산병원 정신과 정석훈 교수는 \"사이코패스는 다른 사람을 학대하거나 다른 사람의 물건을 훔치는 것에 대해 무관심하거나 합리화하는 성향을 보이고 있으며 폭력적인 성격에도 불구하고 외면적으로는 말주변이 좋고 피상적인 매력을 가지는 경우가 있다\"고 설명했다.\n정 교수는 \"이런 점들을 종합적으로 고려할 때 강호순은 전형적인 사이코패스일 가능성이 있다\"고 말했다.\n(서울=연합뉴스)\n<span cl-ass=\"f11 7d7d_color \" style=\"float: left;\">최종편집 : 2009-01-30 15:37	<hr cl-ass=\"hide\"> <table cl-ass=\"subject_table\">인과의 법을 벗어 나는 경우는 없다.   글쓴이: <a cl-ass=\"skinTxt p11\" href=\"http://cafe412.daum.net/_c21_/bbs_search_read?grpid=sD6J&fldid=Tzl&datanum=689&contentval=&docid=sD6J\nTzl\n689\n20090131235531&q=%B0%AD%C8%A3%BC%F8%20%B5%BF%B9%B0%C7%D0%B4%EB&srchid=CCBsD6J\nTzl\n689\n20090131235531&search=true#\" target=\"_blank\" & #111;& #110;click=\"hideLayerAll('member'); showSideView(this, '-th92vaQwJ90', '', 'John(\\uBC15\\uC885\\uD638)'); return false;\">John(박종호) <p cl-ass=\"bar\"><span cl-ass=\"fr num1\"><a cl-ass=\"num1\" href=\"http://cafe.daum.net/hwagyenglish/Tzl/689\" target=\"_blank\">http://cafe.daum.net/hwagyenglish/Tzl/689 .bbs_content p{margin:0px;} <div cl-ass=\"bbs_co...";
		//System.out.println(Remove.htmlTag("&lt;예언&gt; &quot;경계 중에도 정부청사 뚫려&quot;"));
//		String test = "<!-- 본문 內 광고 --><!-- // 본문 內 광고 --><!-- TV플레이어 --><!-- // TV플레이어 --><!-- 본문 내용 --><p></p> <div class=\"videos\">  <object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=10,0,0,0\" width=\"100%\" height=\"200px\" id=\"201410021103565873\" title=\"[VPR] LG전자, 다문화가정 위한 쿠킹 클래스 개최\"><param name=\"allowScriptAccess\" value=\"always\"></param><param name=\"allowNetworking\" value=\"all\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"movie\" value=\"http://www.tagstory.com/player/basic/100625023\"></param></object> </div> <div class=\"videos\">  <object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=10,0,0,0\" width=\"100%\" height=\"200px\" title=\"[VPR] LG전자, 다문화가정 위한 쿠킹 클래스 개최\"><br /></object> </div> <div class=\"videos\">  <object classid=\"clsid:d27cdb6e-ae6d-11cf-96b8-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=10,0,0,0\" width=\"100%\" height=\"200px\" title=\"[VPR] LG전자, 다문화가정 위한 쿠킹 클래스 개최\"><p>■ 30일 LG전자 베스트샵 강남본점으로 베트남출신 주부 초청해 무료 요리강좌 <br />■ 연말까지 중국, 필리핀 등 7개국 다문화가정 주부 1,000여명 대상 전개 <br />■ 한국영업본부장 최상규 부사장 “언어장벽, 문화차이 등으로 어려움 겪고 있는 결혼이주 여성들을 지원하고자 시작한 프로그램으로 나눔경영 지속 강화”<br /><br />LG전자가 ‘온정 캠페인’의 일환으로 다문화가정 대상 ‘함께 만드는 고향 음식, 커가는 사랑’ 프로그램을 전개한다.<br /><br />LG전자는 매 분기마다 장애인, 다문화가정 등 사회취약 계층을 지원하고자 ‘온정(On情) 캠페인’을 진행하며 기업의 사회적 책임을 적극 실천하고 있다.<br /><br />‘함께 만드는 고향 음식, 커가는 사랑’은 국내 요리전문가가 다문화가정 주부들에게 한국 식재료와 조리기구를 활용해 한국식으로 재해석한 고향음식 요리법을 강의하는 프로그램이다. 특히 일반 주부들도 참여해 다문화가정 주부들과 함께 요리를 배우고 육아?가사 노하우를 공유한다.<br /><br />LG전자는 ‘서대문구 다문화가족 지원센터’와 연계해 30일 ‘LG전자 베스트샵 강남본점’에서 베트남출신 주부 및 한국인 주부 약 100명을 대상으로 ‘한국식으로 재해석한 베트남 음식 쿠킹 클래스’를 열었다.<br /><br />LG전자 공식 커뮤니케이션 파트너 ‘더 블로거(The Blogger)’로 활동하고 있는 요리전문 블로거 ‘금별맘’과 ‘비주’가 강사로 나섰다. 이들은 ‘LG 디오스 광파오븐’ 및 프리미엄 가스레인지 ‘히든쿡’을 활용해 ‘한국식 월남쌈’과 ‘파인애플 볶음밥’ 요리법을 강의, 큰 호응을 얻었다.<br /><br />LG전자는 행사 후 베트남어로 번역한 요리책자, 포켓포토로 즉석 인화한 기념 사진 등을 선물했다.<br /><br />이날 참석한 베트남 주부 ‘누엔김똬이’씨는 “한국인 주부들과 함께 요리를 배우고 육아 고민도 나누며 서로의 문화를 더 이해하게 된 의미있는 시간이었다.”고 말했다.<br /><br />LG전자는 연말까지 ‘LG전자 베스트샵’ 30여 곳에서 중국, 말레이시아, 필리핀 등 7개국 다문화가정 주부 1,000여 명을 대상으로 사회공헌활동을 지속 전개할 계획이다.<br /><br />LG전자 한국영업본부장 최상규 부사장은 “언어장벽, 문화차이 등으로 어려움을 겪는 결혼이주여성들을 지원하고자 시작한 프로그램으로 나눔 경영을 지속 강화하겠다.”고 말했다.<br /><br />한편, 프로그램 관련 상세한 소식은 LG전자 공식 페이스북 (facebook.com/theLGstory)의 ‘온정 캠페인’ 코너에서 확인할 수 있다.<br /><br />/파이낸셜뉴스 fncast</p></object> </div> <br /> <p></p><br />";
//		System.out.println(Remove.spaceContinueTab(Remove.enterContinue(Remove.htmlTag(test))));
	}
}