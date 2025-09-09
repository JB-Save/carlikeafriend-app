import "../styles/MessageStyle.css"

export const MessageModalComponent = ({ message, onClose }) => {

  if (!message) {
    //Si no hay mensaje no renderiza el modal
    return null;
  }

  return (
    <div className="message-modal" style={{ display: 'flex' }}> {/* Siempre flex al renderizar */}
      <div className="message-modal-content">
        <span className="message-modal-close" onClick={onClose}>
          &times;
        </span>
        <p>{message}</p>
      </div>
    </div>
  )

}
