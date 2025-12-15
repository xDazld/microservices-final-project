/*global console, window, LanguageModel */

/* jshint esversion: 8, strict: false */

/**
 * DNS Shield - Dashboard Interactive Functions
 */

async function displayRandomFact() {
    const factDiv = document.getElementById('fun-fact');

    try {
        // Check if the Prompt API is available
        if (typeof LanguageModel === 'undefined') {
            console.warn('Prompt API not available, falling back to hardcoded facts');
            displayFallbackFact();
            return;
        }

        // Show loading state
        factDiv.textContent = '🤔 Generating a fun DNS fact...';
        factDiv.style.animation = 'pulse 1s infinite';

        // Create a session with the Prompt API and monitor download progress
        const session = await LanguageModel.create({
            initialPrompts: [{
                role: "system",
                content: "You are a helpful assistant that generates interesting, fun, and educational facts about DNS (Domain Name System), networking, and internet infrastructure. Keep facts concise (1-2 sentences), accurate, and engaging. Start each fact with a relevant emoji."
            }],
            expectedInputs: [
                {type: "text", languages: ["en"]}
            ],
            expectedOutputs: [
                {type: "text", languages: ["en"]}
            ],
            monitor(monitorInstance) {
                monitorInstance.addEventListener("downloadprogress", e => {
                    const percent = Math.round(e.loaded * 100);
                    console.log(`Downloaded ${percent}%`);

                    // Show progress bar in the UI - create elements safely
                    factDiv.textContent = ''; // Clear content
                    const container = document.createElement('div');
                    container.style.textAlign = 'center';

                    const downloadText = document.createElement('div');
                    downloadText.textContent = '📥 Downloading AI model...';
                    container.appendChild(downloadText);

                    const progressBg = document.createElement('div');
                    progressBg.style.cssText = 'margin-top: 10px; background: var(--background-secondary, #f0f0f0); border-radius: 10px; overflow: hidden; height: 20px;';

                    const progressBar = document.createElement('div');
                    progressBar.style.cssText = `width: ${percent}%; height: 100%; background: linear-gradient(90deg, var(--primary-color, #4CAF50), var(--accent-color, #2196F3)); transition: width 0.3s ease;`;
                    progressBg.appendChild(progressBar);
                    container.appendChild(progressBg);

                    const percentText = document.createElement('div');
                    percentText.style.cssText = 'margin-top: 5px; font-size: 0.9em; color: var(--text-secondary, #666);';
                    percentText.textContent = `${percent}%`;
                    container.appendChild(percentText);

                    factDiv.appendChild(container);
                });
            }
        });

        // Generate a DNS fact
        const result = await session.prompt(
            "Generate one interesting and fun fact about DNS, DNS security, DNS protocols, or internet infrastructure."
        );

        // Add animation and display the generated fact
        factDiv.style.animation = 'none';
        setTimeout(() => {
            factDiv.style.animation = 'slideIn 0.5s ease-out';
            factDiv.textContent = result;
        }, 10);

    } catch (error) {
        console.error('Error generating fact with Prompt API:', error);
        displayFallbackFact();
    }
}

function displayFallbackFact() {
    // Fallback to hardcoded facts if Prompt API fails or is unavailable
    const facts = [
        '🌐 DNS requests happen billions of times per day! Without DNS, you\'d have to memorize IP addresses like 93.184.216.34 instead of typing example.com',
        '⚡ The first DNS query to a domain is usually the slowest - that\'s why DNS caching is so important!',
        '🛡️ DNS filtering can block malware, phishing, and ad networks before they even load!',
        '📡 DNS uses UDP port 53 by default, but DNS-over-HTTPS (DoH) uses port 443 for privacy!',
        '🔒 DNSSEC adds cryptographic signatures to DNS records to prevent spoofing attacks!',
        '🎯 Google\'s public DNS (8.8.8.8) processes over 400 billion queries per day!',
        '💾 DNS TTL (Time To Live) tells your computer how long to cache a DNS response!',
        '🚀 DNS queries typically complete in milliseconds - it\'s incredibly fast!',
        '🌍 There are 13 root nameservers managing all top-level domains globally!',
        '🔄 Reverse DNS lookups let you find domain names from IP addresses!'
    ];

    const fact = facts[Math.floor(Math.random() * facts.length)];
    const factDiv = document.getElementById('fun-fact');

    // Add animation
    factDiv.style.animation = 'none';
    setTimeout(() => {
        factDiv.style.animation = 'slideIn 0.5s ease-out';
        factDiv.textContent = fact;
    }, 10);
}

