export const validateCI = (ci: string): boolean => {
  if (ci.length !== 10) return false;
  
  const digits = ci.split('').map(Number);
  const province = parseInt(ci.substring(0, 2));
  
  if (province < 1 || province > 24) return false;
  
  const thirdDigit = digits[2];
  if (thirdDigit > 6) return false;
  
  const coefficients = [2, 1, 2, 1, 2, 1, 2, 1, 2];
  let sum = 0;
  
  for (let i = 0; i < 9; i++) {
    let value = digits[i] * coefficients[i];
    if (value >= 10) value -= 9;
    sum += value;
  }
  
  const verifier = sum % 10 === 0 ? 0 : 10 - (sum % 10);
  return verifier === digits[9];
};

export const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

export const validatePhone = (phone: string): boolean => {
  const phoneRegex = /^[0-9]{9,10}$/;
  return phoneRegex.test(phone);
};
