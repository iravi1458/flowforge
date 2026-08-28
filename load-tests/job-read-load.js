import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 20,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const JOB_ID = __ENV.JOB_ID || 'c5378aab-bbc2-4d51-a6ba-7a03a723b12a';

export default function () {
  const response = http.get(`${BASE_URL}/api/v1/jobs/${JOB_ID}`);

  if (response.status !== 200) {
    console.error(`request failed: status=${response.status} error=${response.error || 'none'}`);
  }

  check(response, {
    'status is 200': (r) => r.status === 200,
    'response contains job id': (r) =>
      r.status === 200 && r.body && r.body.includes(JOB_ID),
  });

  sleep(0.2);
}
