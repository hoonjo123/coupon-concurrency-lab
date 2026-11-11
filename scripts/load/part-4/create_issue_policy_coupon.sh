#!/usr/bin/env bash
# 쿠폰 정보 캐시 실험용 쿠폰을 만들고 ID를 출력한다.
# 시작 시각을 미래로 설정해 issue API가 쿠폰 정보 조회 후 NotStarted로 끝나도록 한다.

# 2099로 설정을 해둬서, coupon service의 로직을 보면, 절대 성립하지 않는다.
# val now = LocalDateTime.now()
  #
  #        if (!coupon.isBookingOpen(now)) {
  #            throw NotStartedException()
  #        } <- 이쪽 부분의 로직
# 이유 : 캐시로 바꾸는게 목표 

set -euo pipefail

curl -fsS -X POST http://localhost:8080/api/coupons \
  -H 'Content-Type: application/json' \
  -d '{"name":"issue policy cache test","totalQuantity":1,"validityDays":7,"startsAt":"2099-01-01T00:00:00"}' \
  | jq -r '.id'
