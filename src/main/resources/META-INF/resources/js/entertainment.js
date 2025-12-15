/*global window, formatNumber */
/* jshint esversion: 6, strict: false */

/**
 * DNS Shield - Entertainment Module
 * This module provides fun and interactive features for the frontend
 */

/**
 * Configuration constants to avoid magic numbers
 */
const RIPPLE_REMOVE_DELAY_MS = 600;
const PULSE_DEFAULT_DURATION_MS = 500;
const SHAKE_DEFAULT_DURATION_MS = 500;
const TOAST_DEFAULT_DURATION_MS = 3000;
const TOAST_FADE_OUT_MS = 300;
const TYPEWRITER_DEFAULT_SPEED_MS = 50;

/**
 * Helper to safely append to the DOM body or documentElement for XHTML compatibility
 */
function appendToRoot(element) {
    const root = document.body || document.documentElement;
    root.appendChild(element);
}

/**
 * Create a floating text message that fades away
 */
function createFloatingMessage(text, x, y) {
    const message = document.createElement('div');
    message.className = 'floating-message';
    message.textContent = text;
    message.style.left = `${x}px`;
    message.style.top = `${y}px`;
    // Use safe appender to avoid XHTML warnings
    appendToRoot(message);

    const duration = 1000;
    const start = performance.now();

    const animate = (time) => {
        const elapsed = time - start;
        const progress = Math.min(elapsed / duration, 1);
        if (progress >= 1) {
            message.remove();
            return;
        }
        // Ensure style.opacity receives a string value
        message.style.opacity = String(1 - progress);
        message.style.transform = `translateY(-${progress * 20}px)`;
        requestAnimationFrame(animate);
    };
    requestAnimationFrame(animate);
}

/**
 * Create a ripple effect on click
 */
function createRipple(event) {
    const ripple = document.createElement('span');
    ripple.className = 'ripple';
    const rect = event.currentTarget.getBoundingClientRect();
    ripple.style.left = `${event.clientX - rect.left}px`;
    ripple.style.top = `${event.clientY - rect.top}px`;
    event.currentTarget.appendChild(ripple);
    // Use constant instead of magic number
    setTimeout(() => ripple.remove(), RIPPLE_REMOVE_DELAY_MS);
}

/**
 * Stats counter animation - animates numbers counting up
 */
function animateCounter(element, endValue, duration = 1000) {
    const startValue = parseInt(element.textContent || '0', 10) || 0;
    const startTime = performance.now();

    const animate = (time) => {
        const progress = Math.min((time - startTime) / duration, 1);
        const value = Math.floor(startValue + (endValue - startValue) * progress);
        element.textContent = String(value);
        if (progress < 1) {
            requestAnimationFrame(animate);
        }
    };
    requestAnimationFrame(animate);
}

/**
 * Create a celebration effect
 */
function createCelebration() {
    const colors = ['#FF6347', '#FFD700', '#32CD32', '#1E90FF', '#FF69B4'];
    const count = 100;

    for (let i = 0; i < count; i++) {
        const confetti = document.createElement('div');
        confetti.className = 'confetti';
        confetti.style.backgroundColor = colors[Math.floor(Math.random() * colors.length)];
        confetti.style.width = `${Math.random() * 8 + 4}px`;
        confetti.style.height = confetti.style.width;
        confetti.style.borderRadius = '50%';
        confetti.style.position = 'fixed';
        confetti.style.left = `${Math.random() * 100}%`;
        confetti.style.top = '-10px';
        confetti.style.opacity = '1';
        // Safe append
        appendToRoot(confetti);

        const fallDuration = Math.random() * 2000 + 3000;
        confetti.animate([
            {transform: 'translateY(0)', opacity: 1},
            {transform: 'translateY(100vh)', opacity: 0}
        ], {
            duration: fallDuration,
            easing: 'ease-in',
            fill: 'forwards'
        }).onfinish = () => confetti.remove();
    }
}

/**
 * Pulse animation on elements
 */
function pulseElement(element, duration = PULSE_DEFAULT_DURATION_MS) {
    element.animate([
        {transform: 'scale(1)'},
        {transform: 'scale(1.05)'},
        {transform: 'scale(1)'}
    ], {
        duration,
        easing: 'ease-in-out'
    });
}

/**
 * Shake animation for errors
 */
function shakeElement(element, duration = SHAKE_DEFAULT_DURATION_MS) {
    element.animate([
        {transform: 'translateX(0)'},
        {transform: 'translateX(-3px)'},
        {transform: 'translateX(3px)'},
        {transform: 'translateX(0)'}
    ], {
        duration,
        easing: 'ease-in-out'
    });
}

/**
 * Highlight element with glow effect
 */
function glowElement(element, duration = 1000) {
    element.animate([
        {boxShadow: '0 0 0px rgba(255, 215, 0, 0.0)'},
        {boxShadow: '0 0 12px rgba(255, 215, 0, 0.8)'},
        {boxShadow: '0 0 0px rgba(255, 215, 0, 0.0)'}
    ], {
        duration,
        easing: 'ease-in-out'
    });
}

/**
 * Create a toast-like notification
 */
function showToast(message, type = 'info', duration = TOAST_DEFAULT_DURATION_MS) {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    appendToRoot(toast);

    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), TOAST_FADE_OUT_MS);
    }, duration);
}

/**
 * Get color by type
 */
function getTypeColor(type) {
    const colors = {
        success: '#28a745',
        danger: '#dc3545',
        warning: '#ffc107',
        info: '#4695EB'
    };
    return colors[type] || colors.info;
}

/**
 * Parallax scroll effect
 */
function enableParallax() {
    const elements = document.querySelectorAll('[data-parallax]');
    window.addEventListener('scroll', () => {
        elements.forEach((el) => {
            const speed = parseFloat(el.getAttribute('data-parallax')) || 0.2;
            // Use window.scrollY instead of deprecated pageYOffset
            const yPos = window.scrollY * speed;
            el.style.transform = `translateY(${yPos}px)`;
        });
    });
}

/**
 * Typewriter effect for text
 */
function typewriterEffect(element, text, speed = TYPEWRITER_DEFAULT_SPEED_MS) {
    let i = 0;
    element.textContent = '';

    function type() {
        if (i < text.length) {
            element.textContent += text.charAt(i);
            i += 1;
            setTimeout(type, speed);
        }
    }
    type();
}

/**
 * Fun loading animation
 */
function createLoadingAnimation() {
    const loader = document.createElement('div');
    loader.className = 'loader';
    appendToRoot(loader);
    return {
        stop: () => loader.remove()
    };
}

/**
 * Get random fun message
 */
function getRandomFunMessage() {
    const messages = [
        '🎉 You rock!', '🚀 Keep going!', '✨ Nice click!', '🔥 That was awesome!', '💡 Great idea!'
    ];
    return messages[Math.floor(Math.random() * messages.length)];
}

/**
 * Create a floating heart or star effect
 */
function createFloatEffect(emoji = '❤️') {
    const float = document.createElement('div');
    float.className = 'float-emoji';
    float.textContent = emoji;
    appendToRoot(float);
    const duration = Math.random() * 1000 + 1000;
    float.animate([
        {transform: 'translateY(0)', opacity: 1},
        {transform: 'translateY(-80px)', opacity: 0}
    ], {
        duration,
        easing: 'ease-out',
        fill: 'forwards'
    }).onfinish = () => float.remove();
}

// Expose utilities to a single namespace to avoid unused warnings and enable usage across the app
window.Entertainment = {
    createFloatingMessage,
    createRipple,
    animateCounter,
    createCelebration,
    pulseElement,
    shakeElement,
    glowElement,
    showToast,
    enableParallax,
    typewriterEffect,
    createLoadingAnimation,
    getRandomFunMessage,
    createFloatEffect
};
