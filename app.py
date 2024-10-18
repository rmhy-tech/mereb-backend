from flask import Flask, jsonify, request
import subprocess
import os
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

@app.route('/run-script', methods=['POST'])
def run_script():
    try:
        subprocess.run(["python", "build_and_deploy.py"], check=True)
        return jsonify({"message": "Script executed successfully"}), 200
    except subprocess.CalledProcessError as e:
        return jsonify({"error": str(e)}), 500

def parse_env_file():
    env_vars = {}
    with open('.env.services', 'r') as file:
        for line in file:
            if '=' in line:
                key, value = line.strip().split('=', 1)
                if ',' in value:
                    env_vars[key] = value.split(',')
                else:
                    env_vars[key] = value
    return env_vars

@app.route('/get-env', methods=['GET'])
def get_env():
    try:
        env_vars = parse_env_file()
        return jsonify({"env_vars": env_vars}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/update-env', methods=['POST'])
def update_env():
    try:
        new_vars = request.json.get('env_vars')
        with open('.env.services', 'w') as file:
            for key, value in new_vars.items():
                if isinstance(value, list):
                    value = ','.join(value)
                file.write(f'{key}={value}\n')
        return jsonify({"message": "Env file updated"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/get-docker-compose', methods=['GET'])
def get_docker_compose():
    try:
        with open('docker-compose.yml', 'r') as file:
            content = file.read()
        return jsonify({"docker_compose": content}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/update-docker-compose', methods=['POST'])
def update_docker_compose():
    try:
        new_content = request.json.get('docker_compose')
        with open('docker-compose.yml', 'w') as file:
            file.write(new_content)
        return jsonify({"message": "Docker Compose updated"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
