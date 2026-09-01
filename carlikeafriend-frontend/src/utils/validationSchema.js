import * as yup from 'yup';
import { isValidPhoneNumber } from 'react-phone-number-input';
import { isTimeOptionValidForPickup, isTimeOptionValidForReturn } from '../utils/dateHelpers';

// Esquema de validación Formulario de búsqueda de reservas
export const searchSchema = yup.object().shape({
    pickupBranch: yup.object().nullable().required('Debes seleccionar una sucursal.'),
    differentReturnBranch: yup.boolean(),
    returnBranch: yup.object().nullable().when('differentReturnBranch', {
        is: true,
        then: (schema) => schema.required('Selecciona sucursal de entrega.'),
        otherwise: (schema) => schema.nullable()
    }),
    dateRange: yup.array().of(yup.date().nullable())
        .test('is-complete', 'Selecciona recogida y entrega.', (value) => value && value[0] && value[1])
        .test('max-days', 'El alquiler máximo es de 30 días en periodos de 24 horas.', function (value) {
            if (!value || !value[0] || !value[1]) return true;

            //Obtenemos las horas del mismo formulario
            const { pickupTime, returnTime } = this.parent;

            let pickup = new Date(value[0]);
            if (pickupTime) {
                pickup.setHours(pickupTime.getHours(), pickupTime.getMinutes(), 0, 0);
            }

            let dropoff = new Date(value[1]);
            if (returnTime) {
                dropoff.setHours(returnTime.getHours(), returnTime.getMinutes(), 0, 0);
            }

            // Calculamos la diferencia en días (1440 minutos = 24h)
            const diffInMinutes = (dropoff.getTime() - pickup.getTime()) / (1000 * 60);
            const diffDays = Math.max(1, Math.ceil(diffInMinutes / 1440.0));
            return diffDays <= 30;
        }),
    pickupTime: yup.date().nullable().required('Hora requerida.')
        .test('is-future', 'La hora debe ser válida (margen de 10m)', function (value) {
            const { dateRange } = this.parent;
            if (!dateRange || !dateRange[0] || !value) return true;
            // Evaluamos con el Helper Absoluto (incluye el margen de 10 min por red)
            return isTimeOptionValidForPickup(dateRange[0], value);
        }),
    returnTime: yup.date().nullable().required('Hora requerida.')
        .test('is-after-pickup', 'Debe ser al menos 1 hora después', function (value) {
            const { dateRange, pickupTime } = this.parent;
            if (!dateRange || !dateRange[0] || !dateRange[1] || !pickupTime || !value) return true;
            // Evaluamos con el Helper Absoluto (resuelve cruces de medianoche)
            return isTimeOptionValidForReturn(dateRange[0], pickupTime, dateRange[1], value);
        })
});


//----- Validación de datos desde panel de Administración -----

// Creamos una base reutilizable para números requeridos
const requiredNumber = (requiredMessage) =>
    yup.number()
        .transform((value, originalValue) => String(originalValue).trim() === "" ? undefined : value)
        .required(requiredMessage)
        .typeError('Debe ser un número válido');

// Esquema de validación datos de usuario
export const userSchema = yup.object().shape({
    name: yup.string().max(60, 'Máximo 60 caracteres').required('El nombre es obligatorio'),
    lastName: yup.string().max(60, 'Máximo 60 caracteres').required('El apellido es obligatorio'),
    documentType: yup.string().max(35).required('El tipo de documento es obligatorio'),
    documentNumber: yup.string().trim().required('El número de documento es obligatorio').matches(/^[A-Za-z0-9]+(-[A-Za-z0-9]+)*$/, 'Solo se permiten letras, números y guiones intermedios').min(5, 'Debe tener mínimo 5 caracteres').max(20, 'Debe tener máximo 20 caracteres'),
    phoneNumber: yup.string().trim().required('El número de teléfono es obligatorio').test('is-valid-phone', 'El número de teléfono no es válido', (value) => value ? isValidPhoneNumber(value) : false),
    nationality: yup.string().max(25, 'Máximo 25 caracteres').required('La nacionalidad es obligatoria'),
    countryCode: yup.string().length(2, 'Debe ser el código ISO 3166-1 alpha-2').required('El país es obligatorio'),
    stateCode: yup.string().max(10, 'Máximo 10 caracteres').required('El estado/departamento es obligatorio'),
    city: yup.string().max(100, 'Máximo 100 caracteres').required('La ciudad es obligatoria'),
    address: yup.string().max(100, 'Máximo 100 caracteres').required('La dirección es obligatoria'),
    zipCode: yup.string().trim().required('El código postal es obligatorio').matches(/^[A-Za-z0-9]+(?:[- ][A-Za-z0-9]+)*$/, 'Solo se permiten caracteres alfanuméricos con espacios o guiones intermedios').min(3, 'Debe tener mínimo 3 caracteres').max(10, 'Debe tener máximo 10 caracteres'),
    birthDate: yup.date().max(new Date(), 'Debe ser una fecha en el pasado').required('La fecha de nacimiento es obligatoria').typeError('Fecha inválida'),
    driverLicenseNumber: yup.string().trim().required('La licencia es obligatoria').matches(/^[A-Za-z0-9]+(-[A-Za-z0-9]+)*$/, 'Solo se permiten letras, números y guiones intermedios').min(5, 'Debe tener mínimo 5 caracteres').max(20, 'Debe tener máximo 20 caracteres'),
    driverLicenseExpiry: yup.date().min(new Date(), 'No puede ser en el pasado').required('El vencimiento es obligatorio').typeError('Fecha inválida'),
    emergencyContactName: yup.string().max(100, 'Máximo 100 caracteres').required('El nombre del contacto es obligatorio'),
    emergencyContactPhone: yup.string().trim().required('El número de teléfono es obligatorio').test('is-valid-emergency-phone', 'El número de teléfono no es válido', (value) => value ? isValidPhoneNumber(value) : false),
    email: yup.string().required('El correo electrónico es obligatorio').matches(/^[^\s@]+@[^\s@]+\.[a-zA-Z]{2,}$/,
        'El formato del email es inválido (ej: usuario@dominio.com)').max(255, 'El email no debe exceder los 255 caracteres'),
    roleIds: yup.array().of(yup.number()).min(1, 'Debes seleccionar al menos un rol').required('Requerido')
});

// Esquema de validación datos del producto
export const productSchema = yup.object().shape({
    name: yup.string().max(100, 'Máximo 100 caracteres').required('El nombre es obligatorio'),
    makeId: yup.string().required('Debes seleccionar una marca'),
    description: yup.string().max(800, 'Máximo 800 caracteres').required('La descripción es obligatoria'),
    passengerCapacity: requiredNumber('Requerido').integer('Debe ser un número entero').min(2, 'Capacidad mínima es 2').max(7, 'Capacidad máxima es 7'),
    baggageCapacity: requiredNumber('Requerido').integer('Debe ser un número entero').min(1, 'Capacidad mínima es 1').max(4, 'Capacidad máxima es 4'),
    numberOfDoors: requiredNumber('Requerido').integer('Debe ser un número entero').min(2, 'El mínimo de puertas es 2').max(5, 'El máximo de puertas es 5'),
    categories: yup.array().of(yup.number()).min(1, 'Debes seleccionar al menos una categoría').required('Requerido'),
    features: yup.array().of(yup.number()).min(1, 'Debes seleccionar al menos una característica').required('Requerido'),
    policies: yup.array().of(yup.number()).min(1, 'Debes seleccionar al menos una política').required('Requerido')
});

// Esquema de validación datos de la característica
export const categorySchema = yup.object().shape({
    name: yup.string().max(100, 'Máximo 100 caracteres').required('El nombre es obligatorio'),
    description: yup.string().max(500, 'Máximo 500 caracteres').required('La descripción es obligatoria'),
    baseDailyRate: requiredNumber('Requerido').positive('El valor debe ser positivo'),
    priority: requiredNumber('Requerido').integer('Debe ser un número entero').positive('El valor debe ser positivo'),
    baseDepositAmount: requiredNumber('Requerido').positive('El valor debe ser positivo')
});

// Esquema de validación campo único de datos de característica, marca, tipo de política y ciudad 
export const singleFieldSchema = yup.object().shape({
    name: yup.string().max(100, 'Máximo 100 caracteres').required('El nombre es obligatorio')
});

// Esquema de validación datos del rol
export const roleSchema = yup.object().shape({
    name: yup.string().max(25, 'Máximo 25 caracteres').required('El nombre es obligatorio'),
    description: yup.string().max(100, 'Máximo 100 caracteres').required('La descripción es obligatoria'),
    permissions: yup.array().of(yup.number()).min(1, 'Debes seleccionar al menos un permiso').required('Requerido')
});

// Variante de roleSchema sin los permisos, para ser reutilizable en los datos del permiso
export const permissionSchema = roleSchema.omit(['permissions']);

// Esquema de validación datos de la politica
export const policySchema = yup.object().shape({
    name: yup.string().max(30, 'Máximo 30 caracteres').required('El nombre es obligatorio'),
    policyTypeId: yup.string().required('Debes seleccionar el tipo de política'),
    content: yup.string().max(16777215, 'Máximo 16777215 caracteres').required('El contenido es obligatorio')
});

// Esquema de validación datos del vehículo
export const vehicleSchema = yup.object().shape({
    licensePlate: yup.string().trim().required('La placa es obligatoria').matches(/^[A-Za-z0-9]+(?:[- ][A-Za-z0-9]+)*$/,
        'La placa solo puede contener letras, números y guiones o espacios intermedios').min(4, 'La placa debe tener mínimo 4 caracteres').max(10, 'La placa debe tener máximo 10 caracteres'),
    vin: yup.string().trim().uppercase().required('El VIN es obligatorio').matches(/^[A-HJ-NPR-Z0-9]{17}$/i, // La 'i' permite validar temporalmente si digitan en minúscula
        'El VIN debe tener exactamente 17 caracteres alfanuméricos (letras I, O, Q no permitidas)'),
    currentMileage: requiredNumber('Requerido').integer('Debe ser un número entero').min(0, 'El valor mínimo es 0'),
    color: yup.string().max(30, 'Máximo 30 caracteres').required('El color es obligatorio'),
    year: requiredNumber('Requerido').integer('Debe ser un número entero').positive('El valor debe ser positivo'),
    productId: yup.string().required('Debes seleccionar un producto'),
    currentBranchId: yup.string().required('Debes seleccionar una sucursal'),
    status: yup.string().max(15, 'Máximo 15 caracteres').required('El estado es obligatorio')
});

// Esquema de validación datos de la sucursal
export const branchSchema = yup.object().shape({
    name: yup.string().max(100, 'Máximo 100 caracteres').required('El nombre es obligatorio'),
    address: yup.string().max(100, 'Máximo 100 caracteres').required('La dirección es obligatoria'),
    cityId: yup.string().required('Debes seleccionar una ciudad'),
    latitude: requiredNumber('La latitud es obligatoria').min(-90.0, 'El valor mínimo es -90.0').max(90.0, 'El valor máximo es 90.0'),
    longitude: requiredNumber('La longitud es obligatoria').min(-180.0, 'El valor mínimo es -180.0').max(180.0, 'El valor máximo es 180.0')
});

// Esquema de validación datos de configuración financiera
export const financialSchema = yup.object().shape({
    taxRate: requiredNumber('El IVA es obligatorio').min(0, 'El valor mínimo es 0').max(1, 'El valor máximo es 1'),
    defaultTransferFee: requiredNumber('La tarifa es obligatoria').min(0, 'El valor mínimo es 0'),
    basicInsuranceDepositMultiplier: requiredNumber('La tasa es obligatoria').min(0, 'El valor mínimo es 0').max(1, 'El valor máximo es 1'),
    premiumInsuranceDepositMultiplier: requiredNumber('La tasa es obligatoria').min(0, 'El valor mínimo es 0').max(1, 'El valor máximo es 1'),
    fullCoverageDepositMultiplier: requiredNumber('La tasa es obligatoria').min(0, 'El valor mínimo es 0').max(1, 'El valor máximo es 1'),
    insuranceBasicRate: requiredNumber('La tarifa es obligatoria').min(0, 'El valor mínimo es 0'),
    insurancePremiumRate: requiredNumber('La tarifa es obligatoria').min(0, 'El valor mínimo es 0'),
    insuranceFullCoverageRate: requiredNumber('La tarifa es obligatoria').min(0, 'El valor mínimo es 0'),
    penaltyWindowHours: requiredNumber('La tarifa es obligatoria').integer('Debe ser un número entero').min(0, 'El valor mínimo es 0'),
    cancellationPenaltyRate: requiredNumber('La tasa es obligatoria').min(0, 'El valor mínimo es 0').max(1, 'El valor máximo es 1'),
    noShowPenaltyRate: requiredNumber('La tasa es obligatoria').min(0, 'El valor mínimo es 0').max(1, 'El valor máximo es 1'),
    maxRentalDays: requiredNumber('Los días máximos de alquiler son obligatorios').integer('Debe ser un número entero').min(0, 'El valor mínimo es 0'),
});

export const transferFeeSchema = yup.object().shape({
    originBranchId: yup.string().transform((value) => (!value ? null : value)).required('Debes seleccionar una sucursal de origen').notOneOf([yup.ref('destinationBranchId')], 'El origen no puede ser igual al destino'),
    destinationBranchId: yup.string().transform((value) => (!value ? null : value)).required('Debes seleccionar una sucursal de destino').notOneOf([yup.ref('originBranchId')], 'El destino no puede ser igual al origen'),
    feeAmount: requiredNumber('La tarifa es obligatoria').min(0, 'El valor mínimo es 0')
});

export const extrasSchema = yup.object().shape({
    name: yup.string().max(25, 'Máximo 25 caracteres').required('El nombre es obligatorio'),
    description: yup.string().max(100, 'Máximo 100 caracteres').required('Las descripción es obligatoria'),
    currentPrice: requiredNumber('El precio es obligatorio').min(0, 'El valor mínimo es 0'),
    chargeType: yup.string().required('Debes seleccionar un tipo de cargo'),
    maxQuantityPerReservation: requiredNumber('La cantidad máxima es obligatoria').min(0, 'El valor mínimo es 0'),
    maxChargeableDays: requiredNumber('Los días máximos son obligatorios').min(0, 'El valor mínimo es 0')
});

export const inventorySchema = yup.object().shape({
    branchId: yup.string().required('Debes seleccionar una sucursal'),
    addonId: yup.string().required('Debes seleccionar un extra'),
    totalStock: requiredNumber('El stock es obligatorio').min(0, 'El valor mínimo es 0')
});



//----- Validación de datos desde panel de mi cuenta -----

// Variante de userValidationSchema sin los roles, para ser reutilizable en los datos del perfil
export const userProfileSchema = userSchema.omit(['roleIds']);

// Esquema de validación para cambio de contraseña
export const changePasswordSchema = yup.object().shape({
    currentPassword: yup.string().required('La contraseña actual es obligatoria'),
    newPassword: yup.string().required('La nueva contraseña es obligatoria').matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
        'La contraseña debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial.'
    ),
    confirmPassword: yup.string().required('Debes confirmar la nueva contraseña').oneOf([yup.ref('newPassword')], 'Las contraseñas no coinciden')
});


//----- Validación de datos para registro e inicio de sesión  -----

// Esquema de validación para Registro
export const signUpSchema = yup.object().shape({
    name: yup.string().max(60, 'El nombre no debe exceder los 60 caracteres').required('El nombre es obligatorio'),
    lastName: yup.string().max(60, 'El apellido no debe exceder los 60 caracteres').required('El apellido es obligatorio'),
    email: yup.string().required('El correo electrónico es obligatorio').matches(/^[^\s@]+@[^\s@]+\.[a-zA-Z]{2,}$/,
        'El formato del email es inválido (ej: usuario@dominio.com)').max(255, 'El email no debe exceder los 255 caracteres'),
    password: yup.string().required('La contraseña es obligatoria').matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
        'La contraseña debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula, un número y un carácter especial.'
    )
});

// Variante de signUpSchema para ser reutilizable en los datos de inicio de sesión
export const signInSchema = signUpSchema.omit(['name', 'lastName']);



