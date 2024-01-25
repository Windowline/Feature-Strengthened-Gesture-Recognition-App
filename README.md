# Feature-Strengthened Gesture Recognition App
직접 정의한 제스처를 통하여 원하는 프로그램을 실행 (Android App, 2014.08 ~ 2014.11) 
가속도 센서 + Dynamic Time Wrapping 기반
Dynamic Time Wrapping 인식률 향상을 위한 기법으로 하단 논문의 기법을 응용합니다.
https://doi.org/10.3745/KTSDE.2015.4.3.143



정확도나 성능의 비교를 위해서 여러 인식 방법을 시도했었고, 잔재가 남아있습니다.
스크린 On Off버튼을 인식 시작-끝 제어, 가속도 센서의 변화율에 의한 인식 시작-끝 제어 등...

편의성을 강조하기 위해서 가급적 버튼이나 기타 신호없이 
가속도 센서의 변화율만으로 인식을 시작하고 마치는 시기를 결정하고 싶었지만
만족할 만한 결과를 얻진 못했습니다.



