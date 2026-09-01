//Formatea una cadena de texto para extraer la primera palabra en formato Capitalize.
const capitalizeWord = (text) => {
    if (!text) return '';
    const firstWord = text.trim().split(' ');
    return firstWord[0].charAt(0).toUpperCase() + firstWord[0].slice(1).toLowerCase();
};

// Une y formatea el primer nombre y el primer apellido de un usuario.
export const getFormattedName = (user) => {
    if (!user) return 'Usuario Anónimo';

    const firstName = capitalizeWord(user.name);
    const firstLastName = capitalizeWord(user.lastName);

    return `${firstName} ${firstLastName}`.trim() || 'Usuario Anónimo';
};

// Genera las iniciales de un usuario basado en su nombre completo según reglas específicas.
export const getInitials = (name) => {
    const completeName = name?.trim();
    if (!completeName) return 'UA';

    const dividedName = completeName.split(' ');
    const len = dividedName.length;
    let initials = 'UA';

    if (len === 1) {
        initials = dividedName[0].charAt(0);
    } else if (len === 2) {
        initials = `${dividedName[0].charAt(0)}${dividedName[1].charAt(0)}`;
    } else if (len >= 3) {
        initials = `${dividedName[0].charAt(0)}${dividedName[2].charAt(0)}`;
    }

    return initials.toUpperCase();
};

// Adapta un string plano de nombre completo en un objeto estructurado.
export const adaptStringToUserObject = (fullName) => {
    const words = fullName?.trim().split(' ') || [];
    const len = words.length;

    return {
        name: len > 0 ? words[0] : '',
        lastName: len === 2 ? words[1] : (len >= 3 ? words[2] : '')
    };
};


// Devuelve un color de fondo fijo basado en las iniciales del usuario.
export const getAvatarColor = (initials) => {
    if (!initials || initials === 'UA') return '#6c757d'; // Gris por defecto para anónimos

    // Lista de colores modernos y amigables con texto blanco
    const colors = [
        '#2ecc71', // Verde
        '#3498db', // Azul
        '#9b59b6', // Morado
        '#e67e22', // Naranja
        '#e74c3c', // Rojo
        '#1abc9c', // Turquesa
        '#34495e', // Azul Oscuro
        '#d35400', // Calabaza
        '#8e44ad', // Violeta
        '#27ae60'  // Esmeralda
    ];

    // Sumamos el valor numérico ASCII de cada letra de las iniciales
    let charCodeSum = 0;
    for (let i = 0; i < initials.length; i++) {
        charCodeSum += initials.charCodeAt(i);
    }

    // Usamos el residuo (%) para elegir un color de la lista de forma consistente
    const colorIndex = charCodeSum % colors.length;
    return colors[colorIndex];
};

