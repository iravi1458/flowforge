import { useCallback, useEffect, useState } from 'react'
import './App.css'
import JobDetailsModal from './JobDetailsModal'

function App() {
  const [activePage, setActivePage] = useState('dashboard')
  const [jobs, setJobs] = useState([])
  const [deadLetterJobs, setDeadLetterJobs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showCreate, setShowCreate] = useState(false)
  const [jobType, setJobType] = useState('GENERATE_REPORT')
  const [payload, setPayload] = useState('')
  const [maxAttempts, setMaxAttempts] = useState(3)
  const [creating, setCreating] = useState(false)
  const [selectedJob, setSelectedJob] = useState(null)
  const [attempts, setAttempts] = useState([])
  const [attemptsLoading, setAttemptsLoading] = useState(false)

  const loadJobs = useCallback(async () => {
    try {
      setError(null)

      const response = await fetch('/api/v1/jobs')

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }

      setJobs(await response.json())
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadJobs()
  }, [loadJobs])

  useEffect(() => {
    async function loadDeadLetterJobs() {
      try {
        const response = await fetch('/api/v1/dlq')

        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`)
        }

        setDeadLetterJobs(await response.json())
      } catch (err) {
        setError(`Could not load DLQ: ${err.message}`)
      }
    }

    loadDeadLetterJobs()
  }, [])

  async function openJob(job) {
    setSelectedJob(job)
    setAttempts([])
    setAttemptsLoading(true)

    try {
      const response = await fetch(`/api/v1/jobs/${job.id}/attempts`)

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }

      setAttempts(await response.json())
    } catch (err) {
      setError(`Could not load attempts: ${err.message}`)
    } finally {
      setAttemptsLoading(false)
    }
  }

  async function createJob(event) {
    event.preventDefault()
    setCreating(true)

    try {
      const response = await fetch('/api/v1/jobs', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Idempotency-Key': crypto.randomUUID(),
        },
        body: JSON.stringify({
          jobType,
          payload,
          maxAttempts: Number(maxAttempts),
        }),
      })

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
      }

      setPayload('')
      setShowCreate(false)
      await loadJobs()
    } catch (err) {
      setError(`Could not create job: ${err.message}`)
    } finally {
      setCreating(false)
    }
  }

  const running = jobs.filter((job) => job.status === 'RUNNING').length
  const succeeded = jobs.filter((job) => job.status === 'SUCCEEDED').length
  const failed = jobs.filter((job) => job.status === 'FAILED').length
  const scheduledJobs = jobs.filter((job) => job.scheduledAt)
  const visibleJobs = activePage === 'scheduled' ? scheduledJobs : jobs

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <h2>FlowForge</h2>
          <span>Workflow Engine</span>
        </div>

        <nav>
          <button
            className={`nav-item ${activePage === 'dashboard' ? 'active' : ''}`}
            onClick={() => setActivePage('dashboard')}
          >
            Dashboard
          </button>

          <button
            className={`nav-item ${activePage === 'jobs' ? 'active' : ''}`}
            onClick={() => setActivePage('jobs')}
          >
            Jobs
          </button>

          <button
            className={`nav-item ${activePage === 'scheduled' ? 'active' : ''}`}
            onClick={() => setActivePage('scheduled')}
          >
            Scheduled
          </button>

          <button
            className={`nav-item ${activePage === 'dlq' ? 'active' : ''}`}
            onClick={() => setActivePage('dlq')}
          >
            Dead Letter Queue
          </button>
        </nav>
      </aside>

      <main className="main">
        <header className="header">
          <div>
            <h1>
              {activePage === 'dashboard' && 'Dashboard'}
              {activePage === 'jobs' && 'Jobs'}
              {activePage === 'scheduled' && 'Scheduled Jobs'}
              {activePage === 'dlq' && 'Dead Letter Queue'}
            </h1>
            <p>
              {activePage === 'dashboard' && 'Distributed job execution overview'}
              {activePage === 'jobs' && 'All jobs processed by FlowForge'}
              {activePage === 'scheduled' && 'Jobs submitted for delayed execution'}
              {activePage === 'dlq' && 'Permanently failed jobs'}
            </p>
          </div>

          <button
            className="primary-button"
            onClick={() => setShowCreate(true)}
          >
            + Create Job
          </button>
        </header>

        <section className="stats">
          <div className="stat-card">
            <span>Total Jobs</span>
            <strong>{jobs.length}</strong>
          </div>

          <div className="stat-card">
            <span>Running</span>
            <strong>{running}</strong>
          </div>

          <div className="stat-card">
            <span>Succeeded</span>
            <strong>{succeeded}</strong>
          </div>

          <div className="stat-card">
            <span>Failed</span>
            <strong>{failed}</strong>
          </div>
        </section>

        <section className="panel">
          <div className="panel-header">
            <div>
              <h2>
                {activePage === 'dlq'
                  ? 'Dead Letter Queue'
                  : activePage === 'scheduled'
                    ? 'Scheduled Jobs'
                    : 'Recent Jobs'}
              </h2>
              <p>
                {activePage === 'dlq'
                  ? 'Jobs that exhausted all retry attempts'
                  : activePage === 'scheduled'
                    ? 'Jobs submitted for delayed execution'
                    : 'Latest jobs processed by FlowForge'}
              </p>
            </div>
          </div>

          <table>
            <thead>
              {activePage === 'dlq' ? (
                <tr>
                  <th>Job ID</th>
                  <th>Attempts</th>
                  <th>Error</th>
                  <th>Dead Lettered</th>
                </tr>
              ) : (
                <tr>
                  <th>Job ID</th>
                  <th>Type</th>
                  <th>Status</th>
                  <th>Attempts</th>
                  <th>{activePage === 'scheduled' ? 'Scheduled For' : 'Created'}</th>
                </tr>
              )}
            </thead>

            <tbody>
              {activePage === 'dlq' ? (
                deadLetterJobs.length === 0 ? (
                  <tr>
                    <td colSpan="4" className="empty-state">
                      No dead-letter jobs found.
                    </td>
                  </tr>
                ) : (
                  deadLetterJobs.map((entry) => (
                    <tr key={entry.id}>
                      <td>{entry.jobId.slice(0, 8)}...</td>
                      <td>{entry.attemptCount}</td>
                      <td>{entry.errorMessage || '—'}</td>
                      <td>{new Date(entry.createdAt).toLocaleString()}</td>
                    </tr>
                  ))
                )
              ) : (
                <>
                  {loading && (
                    <tr>
                      <td colSpan="5" className="empty-state">
                        Loading jobs...
                      </td>
                    </tr>
                  )}

                  {error && (
                    <tr>
                      <td colSpan="5" className="empty-state">
                        {error}
                      </td>
                    </tr>
                  )}

                  {!loading &&
                    !error &&
                    visibleJobs.slice(0, 50).map((job) => (
                      <tr
                        key={job.id}
                        className="job-row"
                        onClick={() => openJob(job)}
                      >
                        <td>{job.id.slice(0, 8)}...</td>
                        <td>{job.jobType}</td>
                        <td>{job.status}</td>
                        <td>{job.attemptCount}/{job.maxAttempts}</td>
                        <td>
                          {new Date(
                            activePage === 'scheduled'
                              ? job.scheduledAt
                              : job.createdAt
                          ).toLocaleString()}
                        </td>
                      </tr>
                    ))}
                </>
              )}
            </tbody>
          </table>
        </section>
      </main>

      <JobDetailsModal
        job={selectedJob}
        attempts={attempts}
        loading={attemptsLoading}
        onClose={() => setSelectedJob(null)}
      />

      {showCreate && (
        <div className="modal-backdrop">
          <div className="modal">
            <h2>Create Job</h2>
            <p>Submit a new job to the FlowForge execution engine.</p>

            <form onSubmit={createJob}>
              <label>
                Job Type
                <select
                  value={jobType}
                  onChange={(event) => setJobType(event.target.value)}
                >
                  <option value="GENERATE_REPORT">GENERATE_REPORT</option>
                  <option value="SEND_EMAIL">SEND_EMAIL</option>
                </select>
              </label>

              <label>
                Payload
                <textarea
                  value={payload}
                  onChange={(event) => setPayload(event.target.value)}
                  placeholder="Enter job payload"
                  rows="4"
                />
              </label>

              <label>
                Max Attempts
                <input
                  type="number"
                  min="1"
                  max="10"
                  value={maxAttempts}
                  onChange={(event) => setMaxAttempts(event.target.value)}
                />
              </label>

              <div className="modal-actions">
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => setShowCreate(false)}
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="primary-button"
                  disabled={creating}
                >
                  {creating ? 'Creating...' : 'Create Job'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default App
