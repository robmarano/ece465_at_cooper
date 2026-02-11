import os

# Configuration
NODES = ['/tmp/node_a', '/tmp/node_b']
IMAGES = ['img_100.jpg', 'img_200.jpg', 'img_300.jpg', 'img_456.jpg', 'img_789.jpg']

def setup_environment():
    print("--- Setting up Environment ---")
    for node in NODES:
        if not os.path.exists(node):
            os.makedirs(node)
            print(f"Created directory: {node}")
        
        # Create dummy images
        for img in IMAGES:
            file_path = os.path.join(node, img)
            if not os.path.exists(file_path):
                with open(file_path, 'w') as f:
                    f.write("This is a dummy image file content.")
                print(f"Created dummy file: {file_path}")
            else:
                print(f"File exists: {file_path}")

    print("\nEnvironment ready. You can now run the CDN examples.")

if __name__ == "__main__":
    setup_environment()
