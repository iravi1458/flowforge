import { useCallback, useEffect, useState } from 'react'
import './App.css'

function App() {
  const [jobs, setJobs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showCreate, setShowCreate] = useState(false)
  const [jobType, setJobType] = useState('GENERATE_REPORT')
  const [payload, setPayload] = useState('')
  const [maxAttempts, setMaxAttempts] = useState(3)
  const [creating, setCreating] = useState(false)

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

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <h2>FlowForge</h2>
          <span>Workflow Engine</span>
        </div>

        <nav>
          <button className="nav-item active">Dashboard</button>
          <button className="nav-item">Jobs</button>
          <button className="nav-item">Scheduled</button>
          <button className="nav-item">Dead Letter Queue</button>
        </nav>
      </aside>

      <main className="main">
        <header className="header">
          <div>
            <h1>Dashboard</h1>
            <p>Distributed job execution overview</p>
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
              <h2>Recent Jobs</h2>
              <p>Latest jobs processed by FlowForge</p>
            </div>
          </div>

          <table>
            <thead>
              <tr>
                <th>Job ID</th>
                <th>Type</th>
                <th>Status</th>
                <th>Attempts</th>
                <th>Created</th>
              </tr>
            </thead>

            <tbody>
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
                jobs.slice(0, 10).map((job) => (
                  <tr key={job.id}>
                    <td>{job.id.slice(0, 8)}...</td>
                    <td>{job.jobType}</td>
                    <td>{job.status}</td>
                    <td>{job.attemptCount}/{job.maxAttempts}</td>
                    <td>{new Date(job.createdAt).toLocaleString()}</td>
                  </tr>
                ))}
            </tbody>
          </table>
        </section>
      </main>

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
