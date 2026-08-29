function JobDetailsModal({ job, attempts, loading, onClose }) {
  if (!job) {
    return null
  }

  return (
    <div className="modal-backdrop">
      <div className="modal job-details-modal">
        <h2>Job Details</h2>
        <p>{job.id}</p>

        <div className="job-details-grid">
          <div>
            <span>Type</span>
            <strong>{job.jobType}</strong>
          </div>
          <div>
            <span>Status</span>
            <strong>{job.status}</strong>
          </div>
          <div>
            <span>Attempts</span>
            <strong>{job.attemptCount}/{job.maxAttempts}</strong>
          </div>
          <div>
            <span>Created</span>
            <strong>{new Date(job.createdAt).toLocaleString()}</strong>
          </div>
        </div>

        <h3>Attempt History</h3>

        {loading ? (
          <p>Loading attempts...</p>
        ) : attempts.length === 0 ? (
          <p>No execution attempts recorded.</p>
        ) : (
          <div className="attempt-list">
            {attempts.map((attempt) => (
              <div className="attempt-card" key={attempt.id}>
                <div>
                  <strong>Attempt {attempt.attemptNumber}</strong>
                  <span>{attempt.status}</span>
                </div>

                <small>
                  {new Date(attempt.startedAt).toLocaleString()}
                </small>

                {attempt.errorMessage && (
                  <p className="attempt-error">{attempt.errorMessage}</p>
                )}
              </div>
            ))}
          </div>
        )}

        <div className="modal-actions">
          <button
            type="button"
            className="secondary-button"
            onClick={onClose}
          >
            Close
          </button>
        </div>
      </div>
    </div>
  )
}

export default JobDetailsModal
