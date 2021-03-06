# App_making
[모바일프로그래밍 기말프로젝트 최종보고서]
스마트ict융합공학과 201713059 백종현

# 주제 : [학업향상을 위한 2G폰 테마]
## 1. 개요 및 목표
### A. 개요
- 독창적인 작품을 위해서는, 가장 먼저 시중에 나와있지 않은 어플리케이션이어야 된다고 생각했다. 따라서 앱스토어나 플레이스토어, 티스토어 등 다양한 어플리케이션 설치 스토어에 들어가 본인이 구상한 어플리케이션이 존재하는지에 대해 비교해보고, 한번도 만들어지지 않았던 2G폰 구축 주제를 선정하게 되었음.
- 시험기간이나 공부할 시간이 절실히 필요하지만, 스마트폰 때문에 집중력이 흐트러져 공부시간이 감소함을 대학 시험기간에 여러 번 느낌. 고등학교 시절 2G폰을 이용하여 공부시간을 배로 늘렸던 경험들을 떠올려 스마트폰을 2G폰처럼 구축한다면 스마트폰에 공부시간에 빼앗기지 않을 수 있을 것임.

### B. 목표
- 스마트폰에서 전화, 문자 및 공부시간 체크 및 현재시간 외에 다른 기능을 사용할 수 없도록, 즉 과거 2G폰 이외에 기능을 사용할 수 없도록 구축하는 시스템
- 다른 어플을 사용하려고 하거나, 이 애플리케이션을 앱 안에서의 종료버튼이 아닌 비정상적인 방식으로 강제 종료하려고 한다면, 이 시스템을 다시 실행하도록 구축.

## 2. 프로젝트 개발 환경 
I. Language
JAVA
II. IDE
Android Studio
III. 데이터 저장
안드로이드 시스템을 이용해 임의의 파일에 저장.

## 3. 프로젝트 디자인
프로젝트에 대한 디자인 계획으로, 제작 후의 클래스나 변수 및 메소드들의 변화가 있을 수 있음.
모든 기능들을 Main클래스에 넣는 것이 아닌, 각각 기능들 별로 Activity를 구성하여 class를 나누었음. 따라서 전화 기능, 문자(SMS) 기능, 캘린더를 통한 공부시간 체크 기능, 메모 기능, 백그라운드 설정기능 및 모든 기능들을 총 담당하는 Main클래스로 나누어 총 6가지 클래스를 사용함.

### A. 액티비티 클래스 6가지에 대한 설명
I. MainAcitivity
activity.xml의 모든 위젯들과 기능들을 담당하며, 전화 버튼을 클릭 시에는 전화 엑티비티로 이동시키고 SMS버튼을 클릭 할 경우에는 SMS엑티비티로 이동하는 등의 큰 틀과 앱의 기능 사용을 최적화 시키기 위해 제작함. 또한 이 앱이 강제적으로 종료되거나, 이 어플리케이션을 사용하면서 다른 게임이나 위젯들을 사용하려고 하면 그렇게 하지 못하도록 구축하기 위한 클래스.

II. SMSActivity
SMS 문자가 오면 문자를 보낸 발신번호와 내용, 시간을 출력해줌. 또한 Vibrator 객체를 통하여 핸드폰에 진동이 울리도록 만듬. 혹은 SMS를 보내야 하는 경우, 전화번호와 문자메시지를 입력 시 그 전화번호에 문자메시지를 보낼 수 있도록 클래스를 구축.

III. CallActivity
상대방에게 전화를 걸고 싶은 경우, 메인에서 전화 Fragment 이동 후 그 Fragment의 EditText에 전화번호를 입력하고, 전화하기 버튼을 누르게 되면 전화가 가능하도록 클래스를 구축. 전화가 오는 경우는 따로 구현할 필요 없이, 시스템에 있는 전화 받는 기능을 사용할 예정.

IV. StudyActivity
본인이 공부를 몇 시간 했는지 알아보기 위해 StudyActivity에서 스톱워치를 제공해주고, 날마다의 공부시간을 캘린더에 기록하여 월별, 주별, 일별로 공부시간을 알 수 있도록 하기위에 만드는 엑티비티
V. MemoActivity
 메모하고 싶은 내용을 메모하고, 저장할 수 있는 엑티비티.

VI. BackgroundActivity
 처음 이 앱에 들어갔을 때, 바탕화면 설정을 하도록 하는 엑티비티. 시간 당 코인이 존재하여, 그 코인으로 바탕화면을 바꿀 수 있음.


<img width="80%" src = "https://user-images.githubusercontent.com/81180977/153762002-de9a5faa-241d-43c3-915f-f2a5cd6afec9.png" />


### B. 클래스 다이어그램 및 클래스 변수와 메소드 구성
 
I. MainActivity의 메소드
- setVariance(void) : activity.xml에서 만든 위젯들과 기능들을 findViewByid를 통해 가져오는 메소드.
- setClock(TextClock textclock) : 현재시간을 알리기 위해 만든 메소드. 현재시간을 초단위로 알려주며, 시간 text의 색깔이나 크기 및 위치 등을 설정할 수 있도록 함.
- onClick(View view) : 다양한 버튼들을 통제하기 위하여, 이 메소드들 호출하게 되면 case문을 통해 View값에 따라 버튼들의 각각이 다른 기능들을 수행할 수 있도록 하기 위하여 만든 클래스.
- print(String msg) : 어떠한 ToastMessage를 출력하고 싶은 경우에 input값으로 그 값을 넣어주면, ToastMessage를 출력하는 메소드.
- restart(void) : 만약 onDestroy 혹은 onPause()등의 앱 정지나 종료가 일어나는 경우, 즉 다른 위젯이나 강제종료를 하려고 하는 경우, 이 reStart 메소드를 이용하여 이 앱이 다시 실행되도록 하는 함수이다. 이 메소드의 역할은 앱을 다시 시작하게 하거나, 이 앱으로 돌아오도록 하게 한다.
- exitProgram(void) : 만약 정상적으로 앱에서 exit버튼을 눌러 나가는 경우에 호출되는 메소드. 앱을 정상적으로 종료시키는 역할을 한다.
- requirePerms() : 안드로이드의 퍼미션을 체크하는 부분. 앱을 실행했을 때, 퍼미션을 허락하지 않는 경우, 앱 사용이 원활하지 않을 수 있음.
- MakeWise() : 바탕화면에 위에 뜨는 명언들을 만드는 메소드.
- readPicture() : BackgroundActivity에서 저장한 바탕화면을 읽어와 바탕화면을 설정하는 부분.

II. SMSActivity 의 메소드
- sendSMS(String, String) : 전화번호와 메시지 내용을 받아, 그 내용을 전화번호에 맞게 메시지를 보내는 메소드.
- readSMS() : 이 SMSActivity에 들어오는 경우, 지금까지 온 메시지의 내용을 읽어야 하는데, 이 메소드를 실행하여 메시지의 내용을 읽어옴.
<같이 사용한 Customer class, SMS Receiver class>
- Customer class는 adapter를 상속받아 사용하여 SMSActivity의 recyclerView에 사용하기 위하여 만든 클래스이다. 
- SMS Receiver class의 경우는 BroadcastReceiver를 상속받아 사용한 클래스이며, 이는 문자 메시지가 오는 경우, 메시지 내용을 시스템 안에 있는 텍스트 파일에 저장하게 된다. 이는 브로드케스트리시버로 구현되어, 이 프로그램이 종료되더라도 실행이 된다.

III. CallActivtiy의 메소드
- sendCall(void) :  전화번호를 editText에 입력하고 전화하기 버튼을 누르면, 상대에게 전화를 걸도록 하는 메소드
- saveCall(String) : 상대방에게 전화를 거는 경우, 그 전화번호를 input값으로 넣어, txt파일에 저장하는 메소드.
- readCall() : 이 엑티비티를 실행하였을 때, 최근 전화목록을 읽어야하는데, 그 전화목록을 txt파일에서 읽어오기 위해서 실행하는 메소드.

IV. StudyActivtiy의 메소드
- Time_interrupt() : 정지 버튼을 누르거나, 탭버튼을 눌러 이 화면 밖으로 나가지게 되는 경우 실행되는 interrupt. 이것은 이 액티비티에서 실행되고 있는 스톱워치 쓰레드를 멈추게 한다.
- saveTime(String) : String값으로 시간 값을 주게 되면, 즉 몇 시간 몇 분 몇 초 동안 공부를 했는지 txt파일에 저장하는 함수.
- saveCoin(String) : 자정이 지나는 경우, StudyActivity에서 시간을 재고 있는 경우에 코인의 수를 저장해야되기 때문에 필요한 함수.
- readCoin() : 코인을 save하는 경우, 코인의 값을 읽어온 후, 시간의 값을 더해줘야 하기 때문에, readCoin을 통해 원래 있는 코인을 읽어오고, 그 다음 더해서 코인을 저장하게 된다. 이를 위해 필요한 메소드이다.
- saveTime_yesterday(String): 자정이 되는 경우 전날의 시간을 저장해야하므로 필요한 메소드.
- readTime_yesterday() :자정이 되는 경우 전날의 시간이 몇시간인지 알아야하므로 필요한 메소드.
- Today(): 오늘의 날짜를 출력해주는 함수. 이는 txt의 파일 name으로 사용되게 된다.
- Yesterday() : 위와 마찬가지로 어제의 날짜를 출력해주는 함수. 이는 txt의 파일 name으로 사용되게 된다.
- initTime() : 이 엑티비티에 들어오는 경우 캘린더 시간 값을 초기화하고 설정해주기 위한 메소드.
- readTime(String) : String, 즉 파일네임에 맞게 시간을 읽어오는 함수. 이는 캘린더에서 어제나 그 달의 다른 날짜의 접근하여 시간을 확인하는 경우 사용하게 되는 메소드 이다.
- readTImeINIT(String) : 이 엑티비티에 들어오는 경우 텍스트 시간 값을 초기화하고 설정해주기 위한 메소드.
<같이 사용한 TimeReset>
- TimeReset은 BroadcastReceiver를 상속받아 사용한 클래스이며, 이는 자정이 지나는 경우 broadcast되어 값이 들어오게 된다. 이를 통하여 전역 메소드인 StudyActivity의 Time_interrupt를 넣게 되고, 이는 만약 시간을 재고 있는 경우는 시간이 멈추고 코인이 저장되게 되며, 그렇지 않은 경우는, 시간 멈추는 것이 없이 그냥 오늘날짜.txt파일에서 시간을 불러와 코인의 수를 증가시키게 된다.

V. MemoActivity
- saveMemo(String) : editText에 작성된 글을 String으로 이 메소드에 넣어 저장시킨다.
- readMemo() : 이 엑티비티를 시작하는 경우, 메모를 텍스트에서 읽어와야 하므로, 이 메소드를 만들게 되었다.

VI. BackgroundActivity
- savePicture(String) : 코인을 이용하여 바탕화면을 사는 경우, 어떤 바탕화면을 구매하였는지 저장이 필요하기 때문에 만든 메소드이다.
- saveCoin(String) : 바탕화면을 구매하는 경우, 그 바탕화면의 가격만큼 coin의 수를 깎고, 그 깎은 만큼의 코인을 제거해야하기 때문에 필요한 메소드이다.
- readCoin() : 원래 얼마의 코인이 있었는지 확인하기 위해 만든 메소드이다.


- 	restart() : 각 엑티비티에 모두 구현된 메소드. 이는 만약 탭 버튼이나 다른 앱을 실행하려고 하였을 때, 이를 방지하기 위해서 만든 메소드이다. 모든 엑티비티에서 실행되게 된다.
 
 
 <img width="50%" src = "https://user-images.githubusercontent.com/81180977/153761955-82c59461-3b04-4a87-bec2-4a18bacd5e04.png" />
 
## <앱을 실행한 모습 및 앱 사용법> </br>
 1.	만약 홈 테마를 이 앱으로 설정하게 된다면, X button을 누르는 것이 아닌 다른 버튼을 눌러서는 이 앱을 나갈 수 없게 된다. 만약 홈 테마를 default로 사용하지 않게 되면, 홉버튼을 누르는 경우 이 앱이 5초 동안 나가지게 되며, 다른 앱 실행이 되버리게 된다.
 2.	중간에 보이는 명언들은 이 앱을 다시 실행하거나, 다른 엑티비티에 들어갔다 나오는 경우, 랜덤으로 나오게 된다.
 3.	오른쪽 상단의 버튼은, 이 화면의 바탕화면을 설정하는 액티비티이다. 여기서 바탕화면을 코인으로 살 수 있다.
 4.	전화 버튼을 통하여, 전화를 할 수 있으며, 최근 발신통화 목록을 볼 수 있다.
 5.	메시지 버튼을 통하여, 메시지를 보낼 수 있고, 최근에 온 20개의 메시지의 내용을 확인할 수 있다.
 6.	3번쨰 버튼은 Memo를 가능하게 하는 앱으로, 내가 메모하고 싶은 내용이 있는 경우, 저기에 메모를 한 후 저장하게 되면 기록이 남게 된다.
 7.	마지막으로, 공부시간 측정 버튼을 누르게 되면, 공부시간을 측정할 수 있고, 이를 통해 1시간 당 1코인을 벌 수 있게 된다. 이는 자정에 코인이 들어오며, 시간이 초기화 된다. 또한 다른 날짜의 공부시간 또한 볼 수 있다.
