import { useEffect, useState } from 'react'
import './App.css'

function App() {
  const [jobs, setJobs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    async function loadJobs() {
      try {
        const response = await fetch('/api/v1/jobs')

        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`)
        }

        const data = await response.json()
        setJobs(data)
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    loadJobs()
  }, [])

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

          <button className="primary-button">+ Create Job</button>
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
                    Failed to load jobs: {error}
                  </td>
                </tr>
              )}

              {!loading && !error && jobs.length === 0 && (
                <tr>
                  <td colSpan="5" className="empty-state">
                    No jobs found.
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
                    <td>
                      {job.attemptCount}/{job.maxAttempts}
                    </td>
                    <td>{new Date(job.createdAt).toLocaleString()}</td>
                  </tr>
                ))}
            </tbody>
          </table>
        </section>
      </main>
    </div>
  )
}

export default App
