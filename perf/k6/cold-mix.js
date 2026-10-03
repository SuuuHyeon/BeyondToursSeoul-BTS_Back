import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const CATEGORIES = ['음식', '쇼핑', '체험관광', '자연관광', '문화관광', '역사관광', '레저스포츠'];
const RANGES = [[0, 0], [0.01, 0.3], [0.31, 0.5], [0.51, 0.7], [0.71, 1.0]];

export const options = {
    scenarios: {
        mix: { executor: 'constant-vus', vus: Number(__ENV.VUS || 50), duration: __ENV.DURATION || '30s' },
    },
};

export function setup() {
    http.del(`${BASE}/actuator/caches`);
}

const pick = (arr) => arr[Math.floor(Math.random() * arr.length)];

export default function () {
    const [min, max] = pick(RANGES);
    const page = Math.floor(Math.random() * 3);
    const url = `${BASE}/api/v1/attractions/page?category=${encodeURIComponent(pick(CATEGORIES))}`
        + `&minScore=${min}&maxScore=${max}&page=${page}&size=10`;
    const res = http.get(url);
    check(res, { 'status 200': (r) => r.status === 200 });
    sleep(0.2);
}