import http from 'k6/http';
import { check } from 'k6';

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const N = Number(__ENV.N || 100);
const URL = `${BASE}/api/v1/attractions/page?category=${encodeURIComponent('음식')}&page=0&size=10`;

export const options = {
    scenarios: {
        burst: { executor: 'shared-iterations', vus: N, iterations: N, maxDuration: '60s' },
    },
};

export function setup() {
    http.del(`${BASE}/actuator/caches`);
}

export default function () {
    const res = http.get(URL);
    check(res, { 'status 200': (r) => r.status === 200 });
}