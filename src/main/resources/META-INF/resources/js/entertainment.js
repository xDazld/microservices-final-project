/**
 * DNS Shield - Entertainment Module
 * This module provides fun and interactive features for the frontend
 */

/**
 * Create a floating text message that fades away
 */
function createFloatingMessage(text, x, y) {
    const message = document.createElement('div');
    message.style.position = 'fixed';
    message.style.left = x + 'px';
    message.style.top = y + 'px';
    message.style.color = 'var(--primary-color)';
    message.style.fontSize = '14px';
    message.style.fontWeight = 'bold';
    message.style.pointerEvents = 'none';
    message.style.zIndex = '9999';
    message.textContent = text;

    document.body.appendChild(message);

    // Animate floating upwards
    const startTime = Date.now();
    const duration = 2000;

    const animate = () => {
        const elapsed = Date.now() - startTime;
        const progress = elapsed / duration;

        if (progress >= 1) {
            message.remove();
            return;
        }

        message.style.opacity = 1 - progress;
        message.style.transform = `translateY(${-progress * 100}px)`;
        requestAnimationFrame(animate);
    };

    animate();
}

/**
 * Create a ripple effect on click
 */
function createRipple(event) {
    const element = event.currentTarget;
    const rect = element.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;

    const ripple = document.createElement('span');
    ripple.style.position = 'absolute';
    ripple.style.width = '20px';
    ripple.style.height = '20px';
    ripple.style.background = 'rgba(255, 255, 255, 0.5)';
    ripple.style.borderRadius = '50%';
    ripple.style.left = x + 'px';
    ripple.style.top = y + 'px';
    ripple.style.pointerEvents = 'none';
    ripple.style.transform = 'scale(0)';
    ripple.style.animation = 'ripple 0.6s ease-out';

    if (element.style.position === 'static') {
        element.style.position = 'relative';
    }

    element.appendChild(ripple);
    setTimeout(() => ripple.remove(), 600);
}

/**
 * Stats counter animation - animates numbers counting up
 */
function animateCounter(element, endValue, duration = 1000) {
    const startValue = 0;
    const startTime = Date.now();

    const animate = () => {
        const elapsed = Date.now() - startTime;
        const progress = Math.min(elapsed / duration, 1);

        const currentValue = Math.floor(startValue + (endValue - startValue) * progress);
        element.textContent = formatNumber(currentValue);

        if (progress < 1) {
            requestAnimationFrame(animate);
        }
    };

    animate();
}

/**
 * Create a celebration effect
 */
function createCelebration() {
    // Create confetti pieces
    const confettiCount = 50;
    const colors = ['#4695EB', '#be9100', '#28a745', '#dc3545', '#ffc107'];

    for (let i = 0; i < confettiCount; i++) {
        const confetti = document.createElement('div');
        confetti.style.position = 'fixed';
        confetti.style.width = Math.random() * 10 + 5 + 'px';
        confetti.style.height = Math.random() * 10 + 5 + 'px';
        confetti.style.backgroundColor = colors[Math.floor(Math.random() * colors.length)];
        confetti.style.borderRadius = '50%';
        confetti.style.left = Math.random() * window.innerWidth + 'px';
        confetti.style.top = '-10px';
        confetti.style.pointerEvents = 'none';
        confetti.style.zIndex = '9998';

        document.body.appendChild(confetti);

        const startX = Math.random() * window.innerWidth;
        const endX = startX + (Math.random() - 0.5) * 400;
        const duration = 2.5 + Math.random() * 1.5;
        const delay = Math.random() * 0.2;

        setTimeout(() => {
            confetti.animate([
                {
                    transform: 'translateY(0) translateX(0) rotate(0deg)',
                    opacity: 1
                },
                {
                    transform: `translateY(${window.innerHeight + 100}px) translateX(${endX - startX}px) rotate(720deg)`,
                    opacity: 0
                }
            ], {
                duration: duration * 1000,
                easing: 'cubic-bezier(0.25, 0.46, 0.45, 0.94)'
            });

            setTimeout(() => confetti.remove(), duration * 1000);
        }, delay * 1000);
    }
}

/**
 * Pulse animation on elements
 */
function pulseElement(element, duration = 500) {
    const keyframes = [
        {transform: 'scale(1)', opacity: 1},
        {transform: 'scale(1.1)', opacity: 0.8},
        {transform: 'scale(1)', opacity: 1}
    ];

    element.animate(keyframes, {
        duration: duration,
        easing: 'ease-in-out'
    });
}

/**
 * Shake animation for errors
 */
function shakeElement(element, duration = 500) {
    const keyframes = [
        {transform: 'translateX(0)'},
        {transform: 'translateX(-5px)'},
        {transform: 'translateX(5px)'},
        {transform: 'translateX(-5px)'},
        {transform: 'translateX(5px)'},
        {transform: 'translateX(0)'}
    ];

    element.animate(keyframes, {
        duration: duration,
        easing: 'ease-in-out'
    });
}

/**
 * Highlight element with glow effect
 */
function glowElement(element, duration = 1000) {
    const originalBox = element.style.boxShadow || '';

    element.animate([
        {boxShadow: 'none'},
        {boxShadow: '0 0 20px rgba(70, 149, 235, 0.8)'},
        {boxShadow: 'none'}
    ], {
        duration: duration,
        easing: 'ease-in-out'
    });

    setTimeout(() => {
        element.style.boxShadow = originalBox;
    }, duration);
}

/**
 * Create a toast-like notification
 */
function showToast(message, type = 'info', duration = 3000) {
    const toast = document.createElement('div');
    toast.style.position = 'fixed';
    toast.style.bottom = '20px';
    toast.style.right = '20px';
    toast.style.padding = '15px 20px';
    toast.style.backgroundColor = getTypeColor(type);
    toast.style.color = 'white';
    toast.style.borderRadius = '5px';
    toast.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.3)';
    toast.style.zIndex = '10000';
    toast.style.minWidth = '250px';
    toast.style.animation = 'slideIn 0.3s ease-out';
    toast.textContent = message;

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'fadeIn 0.3s ease-out reverse';
        setTimeout(() => toast.remove(), 300);
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
    const parallaxElements = document.querySelectorAll('[data-parallax]');

    window.addEventListener('scroll', () => {
        parallaxElements.forEach(element => {
            const speed = element.dataset.parallax || 0.5;
            const yPos = window.pageYOffset * speed;
            element.style.transform = `translateY(${yPos}px)`;
        });
    });
}

/**
 * Typewriter effect for text
 */
function typewriterEffect(element, text, speed = 50) {
    let index = 0;
    element.textContent = '';

    const type = () => {
        if (index < text.length) {
            element.textContent += text.charAt(index);
            index++;
            setTimeout(type, speed);
        }
    };

    type();
}

/**
 * Fun loading animation
 */
function createLoadingAnimation() {
    const dots = ['⠋', '⠙', '⠹', '⠸', '⠼', '⠴', '⠦', '⠧', '⠇', '⠏'];
    let index = 0;

    return () => {
        return dots[index++ % dots.length];
    };
}

/**
 * Get random fun message
 */
function getRandomFunMessage() {
    const messages = [
        '🚀 DNS is faster than a speeding bullet!',
        '🛡️ Your shields are holding!',
        '⚡ Filtering like a boss!',
        '🎯 On target!',
        '💫 DNS magic in progress...',
        '🔒 Keeping you safe!',
        '📡 Signals received!',
        '🎪 Having fun with DNS!',
        '🌟 Shielding complete!',
        '🏆 DNS Champion!'
    ];

    return messages[Math.floor(Math.random() * messages.length)];
}

/**
 * Create a floating heart or star effect
 */
function createFloatEffect(emoji = '❤️') {
    for (let i = 0; i < 5; i++) {
        const float = document.createElement('div');
        float.style.position = 'fixed';
        float.style.left = Math.random() * window.innerWidth + 'px';
        float.style.top = window.innerHeight + 'px';
        float.style.fontSize = '2rem';
        float.style.pointerEvents = 'none';
        float.style.zIndex = '9998';
        float.textContent = emoji;

        document.body.appendChild(float);

        const duration = 3 + Math.random() * 2;
        const offsetX = (Math.random() - 0.5) * 200;

        float.animate([
            {transform: 'translateY(0) translateX(0) scale(1)', opacity: 1},
            {
                transform: `translateY(-${window.innerHeight + 100}px) translateX(${offsetX}px) scale(0)`,
                opacity: 0
            }
        ], {
            duration: duration * 1000,
            easing: 'cubic-bezier(0.25, 0.46, 0.45, 0.94)'
        });

        setTimeout(() => float.remove(), duration * 1000);
    }
}

export {
    createFloatingMessage,
    createRipple,
    animateCounter,
    createCelebration,
    pulseElement,
    shakeElement,
    glowElement,
    showToast,
    getTypeColor,
    enableParallax,
    typewriterEffect,
    createLoadingAnimation,
    getRandomFunMessage,
    createFloatEffect
};

