# Quantum Particle Effect

포션 효과마다 다른 파티클 텍스처를 쓰게 해 주는 Fabric 모드예요. (Minecraft 26.3, 클라이언트 전용)

![왼쪽부터 독, 재생, 성급함(기본 모양)](docs/screenshot.png)

왼쪽부터 독, 재생, 성급함이에요. 성급함은 텍스처를 넣지 않아서 기본 모양 그대로 나와요.

## 설치

- Fabric Loader 0.20.1 이상, Java 25
- `build/libs/quantumparticleeffect-<버전>.jar` 파일을 `mods` 폴더에 넣으면 돼요.
- 클라이언트에만 설치하면 돼요. 서버에는 설치하지 않아도 되고, 일반 서버에서도 동작해요.

## 기본 포함 텍스처

독, 위더, 재생, 신속, 구속, 힘, 화염 저항, 야간 투시, 점프 강화, 수중 호흡, 행운, 느린 낙하.
나머지 효과는 기본 파티클 그대로 나와요.

## 리소스팩으로 텍스처 바꾸기 / 추가하기

효과 `<네임스페이스>:<이름>`의 파티클은 아래 파일을 읽어요.

```
assets/<네임스페이스>/particles/effect/<이름>.json
```

예를 들어 성급함(`minecraft:haste`)에 텍스처를 주려면 리소스팩에 두 파일을 넣으면 돼요.

`assets/minecraft/particles/effect/haste.json`
```json
{
  "textures": ["mypack:effect/haste"]
}
```

`assets/mypack/textures/particle/effect/haste.png` (8×8 권장, **흰색/회색으로** 그리기)

- 텍스처를 여러 장 넣으면 파티클이 사라지는 동안 순서대로 바뀌어요. 기본 `entity_effect.json`과 같은 방식이에요.
- 모드에 들어 있는 텍스처는 같은 경로(`assets/quantumparticleeffect/textures/particle/effect/<이름>.png`)에 그림을 넣어 덮어쓰면 돼요.
- json 파일이 없으면 그 효과는 기본 파티클로 돌아가요.
- 다른 모드가 추가한 효과도 같은 방법으로 쓸 수 있어요.

## 알아 둘 점

- 서버는 효과 종류 대신 **효과 색만** 보내 줘요. 그래서 모드는 색으로 효과를 찾아요.
  - 색이 같은 효과(예: 즉시 회복과 포화)는 먼저 등록된 효과의 텍스처를 써요.
  - 효과가 여러 개 섞인 물약 구름이나 화살은 색이 섞여서 기본 파티클로 나와요.
- 파티클 색은 원래 포션 색을 그대로 따라가요. 게임이 텍스처에 효과 색을 곱해서 입히기 때문에, 텍스처는 흰색/회색 명암으로 그려야 해요. 밝은 부분일수록 포션 색이 선명하게 나와요.
- 시련의 징조, 습격의 징조는 원래부터 전용 파티클이 있어서 바뀌지 않아요.

## Maven

GitHub에서 릴리스를 만들면 GitHub Actions가 GitHub Packages(Maven)에 자동으로 올려요. 릴리스 전에 `gradle.properties`의 `version`을 올려 주세요.

다른 모드에서 쓰려면:

```groovy
repositories {
	maven {
		url = "https://maven.pkg.github.com/qmftm/QuantumParticleEffect"
		credentials {
			username = System.getenv("GITHUB_ACTOR")
			password = System.getenv("GITHUB_TOKEN") // read:packages 권한이 있는 토큰
		}
	}
}

dependencies {
	modImplementation "com.qmftm:quantumparticleeffect:1.0.0"
}
```

GitHub Packages는 공개 패키지도 받을 때 GitHub 토큰이 필요해요.

## 빌드 / 테스트

```
./gradlew build              # build/libs 에 jar 생성
./gradlew runClient          # 개발용 클라이언트 실행
./gradlew runClientGameTest  # 월드를 만들어 효과를 주고 스크린샷 저장
./gradlew publishToMavenLocal # ~/.m2 에 배포해서 확인
```
