# Setup for of local ollama

## Apple Silicon
- If you desire to run ollama locally to use CoverAgent and Gradle here is a command that works for me

### Setup and Explain the command to run ollama 

```bash 
docker run -d --rm --platform linux/arm64 -v ~/code/docker_mnt/llama2:/root/.ollama -p 11434:11434 --memory=10g --name ollama ollama/ollama
```
1.	--platform linux/arm64: Specifies the architecture compatible with Apple Silicon (M1/M2/M3).
2.	-v ollama:/root/.ollama: Mounts a volume for persistent storage of configuration and model files.
3.	-p 11434:11434: Maps the container’s port to the host for API access.
4.	--name ollama: Names the container for easier management.
5.	ollama/ollama: Specifies the Docker image.
6. -d detached mode
7. --rm remove the container when stopped

### Command to start ollama
- Make sure you have updated your Docker Desktop to have at least 10 gig of memory I would make it 16 gig is what i use and works good on a Apple Silicon 36 gig  
```bash
docker exec -it ollama ollama run llama2
```
1. This will start llama2 in the container it will download and pull the model and necessary content to run for the first with the mount 
