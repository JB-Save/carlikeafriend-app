import "../styles/MessageStyle.css"

export const MessageModalComponent = ({ message, onClose }) => {

  if (!message) {
    //Si no hay mensaje no renderiza el modal
    return null;
  }

  return (
    <div className="message-modal" style={{ display: 'flex' }}>
      <div className="message-modal-content">

        <span
          className="message-modal-close"
          onClick={onClose}
          role="button"
          aria-label="Close"
        >
          &times;
        </span>


        <div className="py-3">
          <p className="fs-5 mb-0">{message}</p>
        </div>


        <button
          className="btn mt-3 shadow-sm"
          style={{ backgroundColor: '#2e2e84', color: '#f4f3f2' }}
          onClick={onClose}
        >
          Entendido
        </button>
      </div>
    </div>
  )
}
