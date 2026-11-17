-- KEYS[1] = coupon:{id} (Hash: value, fetchedAtMs), KEYS[2] = lock key
-- ARGV: nowMs, freshMs, lockToken, lockTtlMs
-- 반환: Hit ( 값 반환 ) / STALE_REFRESH ( 값 반환 + 백그라운드 refresh ) / LOAD ( 이 요청이 채움) / WAIT ( 다른 요청이 채우는 중 )

-- HMGET : 해시 멀티 GET , fetchedAtMs를 함께 넣음으로써 시간이 오래걸렸다고 판단이 되면, 백그라운드에서 바로 리프레시 하기 위함
local data = redis.call('HMGET', KEYS[1], 'value', 'fetchedAtMs')
local value, fetchedAtStr = data[1], data[2]

-- 캐시가 없을 때
if not value then
    local acquired = redis.call('SET', KEYS[2], ARGV[3], 'NX', 'PX', ARGV[4])
    if acquired then return {'LOAD', ''} end
    return {'WAIT', ''}
end

local now = tonumber(ARGV[1])
local fetchedAt = tonumber(fetchedAtStr)
if now - fetchedAt < tonumber(ARGV[2]) then
    return {'HIT', value}
end


-- 시간을 초과하면 바로 refresh 시도
-- stale: refresh lock 시도, 못 잡으면 다른 요청이 갱신 중이니 그냥 HIT
local acquired = redis.call('SET', KEYS[2], ARGV[3], 'NX', 'PX', ARGV[4])
if acquired then return {'STALE_REFRESH', value} end
return {'HIT', value}

