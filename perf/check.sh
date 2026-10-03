#!/bin/bash
# 관광지 목록 API가 개선 전후 같은 결과를 내는지 확인한다.
# 탐색 화면(page)은 전체 개수와 첫 페이지 ID, 지도 화면은 개수와 앞 5개 ID를 출력한다.
B=http://localhost:8080/api/v1/attractions
curl -s -X DELETE http://localhost:8080/actuator/caches

# 카테고리: 없음 / 음식 / 영어 소문자 / 영어 대문자 / 없는 카테고리
for c in '' '%EC%9D%8C%EC%8B%9D' 'food' 'Shopping' 'xyz'; do
  echo "page category=[$c]"
  curl -s "$B/page?category=$c&page=0&size=10" | jq -c '{total: .totalElements, ids: [.content[].id]}'
  echo "map  category=[$c]"
  curl -s "$B?category=$c" | jq -c '[.[].id] | {count: length, first: .[0:5]}'
done
